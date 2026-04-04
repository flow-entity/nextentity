package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;

import java.util.ArrayList;
import java.util.List;

///
/// 条件 SQL 语句构建器的抽象基类
///
/// 该类封装了 WHERE 条件表达式的处理逻辑，使用实例字段保存构建上下文。
/// 支持处理 PathNode、LiteralNode、OperatorNode 等各种表达式节点类型，
/// 包括嵌套路径的子查询生成。
///
/// @author HuangChengwei
/// @since 2.0
///
public abstract class AbstractConditionalStatementBuilder {

    protected final StringBuilder sql = new StringBuilder();
    protected final List<Object> params = new ArrayList<>();
    protected final SqlDialect dialect;
    protected final EntityType entityType;
    protected final Metamodel metamodel;
    protected final ExpressionNode whereCondition;

    protected AbstractConditionalStatementBuilder(EntityType entityType,
                                                  Metamodel metamodel,
                                                  ExpressionNode whereCondition,
                                                  SqlDialect dialect) {
        this.entityType = entityType;
        this.metamodel = metamodel;
        this.whereCondition = whereCondition;
        this.dialect = dialect;
    }

    protected String leftQuotedIdentifier() {
        return dialect.leftQuotedIdentifier();
    }

    protected String rightQuotedIdentifier() {
        return dialect.rightQuotedIdentifier();
    }

    /// 添加表名
    protected void appendTableName() {
        sql.append(leftQuotedIdentifier())
                .append(entityType.tableName())
                .append(rightQuotedIdentifier());
    }

    /// 添加 WHERE 条件
    protected void appendWhereCondition() {
        if (whereCondition != null) {
            sql.append(" where ");
            appendExpression(whereCondition);
        }
    }

    /// 处理表达式节点
    protected void appendExpression(ExpressionNode node) {
        if (node instanceof PathNode pathNode) {
            appendPath(pathNode);
        } else if (node instanceof LiteralNode literalNode) {
            sql.append("?");
            params.add(literalNode.value());
        } else if (node instanceof OperatorNode operatorNode) {
            appendOperatorNode(operatorNode);
        }
    }

    /// 处理操作符节点
    protected void appendOperatorNode(OperatorNode node) {
        Operator operator = node.operator();
        List<? extends ExpressionNode> operands = node.operands();

        switch (operator) {
            case AND, OR -> {
                sql.append("(");
                for (int i = 0; i < operands.size(); i++) {
                    if (i > 0) {
                        sql.append(" ").append(operator.sign()).append(" ");
                    }
                    appendExpression(operands.get(i));
                }
                sql.append(")");
            }
            case NOT -> {
                sql.append("not ");
                appendExpression(operands.getFirst());
            }
            case EQ, NE, GT, GE, LT, LE, LIKE -> {
                ExpressionNode leftOperand = operands.get(0);
                ExpressionNode rightOperand = operands.get(1);

                if (leftOperand instanceof PathNode pathNode && pathNode.deep() > 1) {
                    appendNestedPathComparison(pathNode, operator, rightOperand);
                } else {
                    appendExpression(leftOperand);
                    sql.append(" ").append(operator.sign()).append(" ");
                    appendExpression(rightOperand);
                }
            }
            case IN -> {
                ExpressionNode leftOperand = operands.getFirst();

                if (leftOperand instanceof PathNode pathNode && pathNode.deep() > 1) {
                    appendNestedPathIn(pathNode, operands);
                } else {
                    appendExpression(leftOperand);
                    appendIn(operands);
                }
            }
            case IS_NULL -> {
                appendExpression(operands.getFirst());
                sql.append(" is null");
            }
            case IS_NOT_NULL -> {
                appendExpression(operands.getFirst());
                sql.append(" is not null");
            }
            case BETWEEN -> {
                appendExpression(operands.get(0));
                sql.append(" between ");
                appendExpression(operands.get(1));
                sql.append(" and ");
                appendExpression(operands.get(2));
            }
            default -> throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
    }

    /// 处理 IN 操作
    protected void appendIn(List<? extends ExpressionNode> operands) {
        sql.append(" IN (");
        for (int i = 1; i < operands.size(); i++) {
            if (i > 1) sql.append(", ");
            appendExpression(operands.get(i));
        }
        sql.append(")");
    }

    /// 处理路径节点
    protected void appendPath(PathNode path) {
        EntityAttribute attribute = (EntityAttribute) entityType.getAttribute(path);
        sql.append(leftQuotedIdentifier())
                .append(attribute.columnName())
                .append(rightQuotedIdentifier());
    }

    /// Join 信息记录
    protected record JoinInfo(
            String foreignKeyColumn,
            String joinTableName,
            String referencedColumnName,
            String targetColumn,
            EntityAttribute entityAttribute
    ) {
    }

    /// 获取嵌套路径的 Join 信息
    protected JoinInfo getJoinInfo(PathNode nestedPath) {
        Attribute attr = entityType.getAttribute(nestedPath);
        if (!(attr instanceof EntityAttribute entityAttribute)) {
            throw new UnsupportedOperationException("Unsupported nested path: " + nestedPath);
        }

        Schema declareBy = entityAttribute.declareBy();
        if (!(declareBy instanceof JoinAttribute joinAttribute)) {
            return null;
        }

        String foreignKeyColumn = joinAttribute.joinName();
        String joinTableName = joinAttribute.tableName();
        String referencedColumnName = joinAttribute.referencedColumnName();
        if (referencedColumnName == null || referencedColumnName.isEmpty()) {
            referencedColumnName = joinAttribute.id().columnName();
        }
        String targetColumn = entityAttribute.columnName();

        return new JoinInfo(foreignKeyColumn, joinTableName, referencedColumnName, targetColumn, entityAttribute);
    }

    /// 构建嵌套路径子查询前缀
    protected void appendNestedPathSubQueryPrefix(JoinInfo joinInfo) {
        sql.append(leftQuotedIdentifier())
                .append(joinInfo.foreignKeyColumn())
                .append(rightQuotedIdentifier());
        sql.append(" IN (SELECT ");
        sql.append(leftQuotedIdentifier())
                .append(joinInfo.referencedColumnName())
                .append(rightQuotedIdentifier());
        sql.append(" FROM ");
        sql.append(leftQuotedIdentifier())
                .append(joinInfo.joinTableName())
                .append(rightQuotedIdentifier());
        sql.append(" where ");
        sql.append(leftQuotedIdentifier())
                .append(joinInfo.targetColumn())
                .append(rightQuotedIdentifier());
    }

    /// 嵌套路径比较操作子查询
    protected void appendNestedPathComparison(PathNode nestedPath, Operator operator, ExpressionNode rightOperand) {
        JoinInfo joinInfo = getJoinInfo(nestedPath);

        if (joinInfo != null) {
            appendNestedPathSubQueryPrefix(joinInfo);
            sql.append(" ").append(operator.sign()).append(" ");
            appendExpression(rightOperand);
            sql.append(")");
        } else {
            Attribute attr = entityType.getAttribute(nestedPath);
            EntityAttribute entityAttribute = (EntityAttribute) attr;
            sql.append(leftQuotedIdentifier())
                    .append(entityAttribute.columnName())
                    .append(rightQuotedIdentifier());
            sql.append(" ").append(operator.sign()).append(" ");
            appendExpression(rightOperand);
        }
    }

    /// 嵌套路径 IN 操作子查询
    protected void appendNestedPathIn(PathNode nestedPath, List<? extends ExpressionNode> operands) {
        JoinInfo joinInfo = getJoinInfo(nestedPath);

        if (joinInfo != null) {
            appendNestedPathSubQueryPrefix(joinInfo);
            sql.append(" IN (");
            for (int i = 1; i < operands.size(); i++) {
                if (i > 1) sql.append(", ");
                appendExpression(operands.get(i));
            }
            sql.append("))");
        } else {
            Attribute attr = entityType.getAttribute(nestedPath);
            EntityAttribute entityAttribute = (EntityAttribute) attr;
            sql.append(leftQuotedIdentifier())
                    .append(entityAttribute.columnName())
                    .append(rightQuotedIdentifier());
            appendIn(operands);
        }
    }
}