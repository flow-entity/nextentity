package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.jdbc.ConnectionProvider.ConnectionCallback;

import java.sql.SQLException;
import java.util.List;

/// Base class for JDBC conditional operations with common SQL building utilities.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 2.1
abstract class JdbcWhereStepSupport<T> {

    protected final Class<T> entityType;
    protected final Metamodel metamodel;
    protected final ConnectionProvider connectionProvider;
    protected final SqlDialect sqlDialect;
    protected ExpressionNode whereCondition;

    protected JdbcWhereStepSupport(Class<T> entityType,
                                   Metamodel metamodel,
                                   ConnectionProvider connectionProvider,
                                   SqlDialect sqlDialect) {
        this.entityType = entityType;
        this.metamodel = metamodel;
        this.connectionProvider = connectionProvider;
        this.sqlDialect = sqlDialect;
    }

    protected void setWhereCondition(ExpressionNode condition) {
        if (this.whereCondition == null) {
            this.whereCondition = condition;
        } else {
            this.whereCondition = this.whereCondition.operate(Operator.AND, condition);
        }
    }

    protected int appendWhereCondition(StringBuilder sql, List<Object> params,
                                        ExpressionNode node, EntityType entity) {
        if (node instanceof PathNode pathNode) {
            appendPath(sql, pathNode, entity);
        } else if (node instanceof LiteralNode literalNode) {
            sql.append("?");
            params.add(literalNode.value());
        } else if (node instanceof OperatorNode operatorNode) {
            appendOperatorNode(sql, params, operatorNode, entity);
        }
        return params.size();
    }

    private void appendOperatorNode(StringBuilder sql, List<Object> params,
                                     OperatorNode node, EntityType entity) {
        Operator operator = node.operator();
        List<? extends ExpressionNode> operands = node.operands();

        switch (operator) {
            case AND, OR -> {
                sql.append("(");
                for (int i = 0; i < operands.size(); i++) {
                    if (i > 0) {
                        sql.append(" ").append(operator.sign()).append(" ");
                    }
                    appendWhereCondition(sql, params, operands.get(i), entity);
                }
                sql.append(")");
            }
            case NOT -> {
                sql.append("NOT ");
                appendWhereCondition(sql, params, operands.get(0), entity);
            }
            case EQ, NE, GT, GE, LT, LE, LIKE -> {
                ExpressionNode leftOperand = operands.get(0);
                ExpressionNode rightOperand = operands.get(1);

                // 检查是否是嵌套路径的比较操作
                if (leftOperand instanceof PathNode pathNode && pathNode.deep() > 1) {
                    // 嵌套路径：生成子查询
                    appendNestedPathComparison(sql, params, pathNode, operator, rightOperand, entity);
                } else {
                    // 简单路径：直接比较
                    appendWhereCondition(sql, params, leftOperand, entity);
                    sql.append(" ").append(operator.sign()).append(" ");
                    appendWhereCondition(sql, params, rightOperand, entity);
                }
            }
            case IN -> {
                ExpressionNode leftOperand = operands.get(0);

                // 检查是否是嵌套路径的 IN 操作
                if (leftOperand instanceof PathNode pathNode && pathNode.deep() > 1) {
                    // 嵌套路径：生成子查询
                    appendNestedPathIn(sql, params, pathNode, operands, entity);
                } else {
                    // 简单路径：直接处理
                    appendWhereCondition(sql, params, leftOperand, entity);
                    sql.append(" IN (");
                    for (int i = 1; i < operands.size(); i++) {
                        if (i > 1) sql.append(", ");
                        appendWhereCondition(sql, params, operands.get(i), entity);
                    }
                    sql.append(")");
                }
            }
            case IS_NULL -> {
                appendWhereCondition(sql, params, operands.get(0), entity);
                sql.append(" IS NULL");
            }
            case IS_NOT_NULL -> {
                appendWhereCondition(sql, params, operands.get(0), entity);
                sql.append(" IS NOT NULL");
            }
            case BETWEEN -> {
                appendWhereCondition(sql, params, operands.get(0), entity);
                sql.append(" BETWEEN ");
                appendWhereCondition(sql, params, operands.get(1), entity);
                sql.append(" AND ");
                appendWhereCondition(sql, params, operands.get(2), entity);
            }
            default -> throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
    }

    ///
    /// 为嵌套路径的比较操作生成子查询。
    ///
    /// 例如：Employee::getDepartment).get(Department::getName).eq("Engineering")
    /// 生成：department_id IN (SELECT id FROM department WHERE name = ?)
    ///
    /// @param sql SQL 构建器
    /// @param params 参数列表
    /// @param nestedPath 嵌套路径节点
    /// @param operator 比较操作符
    /// @param rightOperand 右操作数
    /// @param entity 实体类型
    private void appendNestedPathComparison(StringBuilder sql, List<Object> params,
                                             PathNode nestedPath, Operator operator,
                                             ExpressionNode rightOperand, EntityType entity) {
        Attribute attr = entity.getAttribute(nestedPath);

        if (attr instanceof EntityAttribute entityAttribute) {
            Schema declareBy = entityAttribute.declareBy();
            if (declareBy instanceof JoinAttribute joinAttribute) {
                // 构建子查询：foreign_key IN (SELECT id FROM table WHERE column OP ?)
                String foreignKeyColumn = joinAttribute.joinName();
                String joinTableName = joinAttribute.tableName();
                String referencedColumnName = joinAttribute.referencedColumnName();
                if (referencedColumnName == null || referencedColumnName.isEmpty()) {
                    referencedColumnName = joinAttribute.id().columnName();
                }
                String targetColumn = entityAttribute.columnName();

                // foreign_key IN (SELECT id FROM table WHERE column OP ?)
                sql.append(leftQuotedIdentifier()).append(foreignKeyColumn).append(rightQuotedIdentifier());
                sql.append(" IN (SELECT ");
                sql.append(leftQuotedIdentifier()).append(referencedColumnName).append(rightQuotedIdentifier());
                sql.append(" FROM ");
                sql.append(leftQuotedIdentifier()).append(joinTableName).append(rightQuotedIdentifier());
                sql.append(" WHERE ");
                sql.append(leftQuotedIdentifier()).append(targetColumn).append(rightQuotedIdentifier());
                sql.append(" ").append(operator.sign()).append(" ");
                // 添加右操作数（值）
                appendWhereCondition(sql, params, rightOperand, entity);
                sql.append(")");
            } else {
                // 回退到简单路径处理
                sql.append(leftQuotedIdentifier()).append(entityAttribute.columnName()).append(rightQuotedIdentifier());
                sql.append(" ").append(operator.sign()).append(" ");
                appendWhereCondition(sql, params, rightOperand, entity);
            }
        } else {
            throw new UnsupportedOperationException("Unsupported nested path: " + nestedPath);
        }
    }

    ///
    /// 为嵌套路径的 IN 操作生成子查询。
    ///
    /// 例如：Employee::getDepartment).get(Department::getName).in("Engineering", "Marketing")
    /// 生成：department_id IN (SELECT id FROM department WHERE name IN (?, ?))
    ///
    /// @param sql SQL 构建器
    /// @param params 参数列表
    /// @param nestedPath 嵌套路径节点
    /// @param operands 操作数列表（第一个是路径，其余是值）
    /// @param entity 实体类型
    private void appendNestedPathIn(StringBuilder sql, List<Object> params,
                                     PathNode nestedPath, List<? extends ExpressionNode> operands,
                                     EntityType entity) {
        Attribute attr = entity.getAttribute(nestedPath);

        if (attr instanceof EntityAttribute entityAttribute) {
            Schema declareBy = entityAttribute.declareBy();
            if (declareBy instanceof JoinAttribute joinAttribute) {
                String foreignKeyColumn = joinAttribute.joinName();
                String joinTableName = joinAttribute.tableName();
                String referencedColumnName = joinAttribute.referencedColumnName();
                if (referencedColumnName == null || referencedColumnName.isEmpty()) {
                    referencedColumnName = joinAttribute.id().columnName();
                }
                String targetColumn = entityAttribute.columnName();

                // foreign_key IN (SELECT id FROM table WHERE column IN (?, ?))
                sql.append(leftQuotedIdentifier()).append(foreignKeyColumn).append(rightQuotedIdentifier());
                sql.append(" IN (SELECT ");
                sql.append(leftQuotedIdentifier()).append(referencedColumnName).append(rightQuotedIdentifier());
                sql.append(" FROM ");
                sql.append(leftQuotedIdentifier()).append(joinTableName).append(rightQuotedIdentifier());
                sql.append(" WHERE ");
                sql.append(leftQuotedIdentifier()).append(targetColumn).append(rightQuotedIdentifier());
                sql.append(" IN (");
                for (int i = 1; i < operands.size(); i++) {
                    if (i > 1) sql.append(", ");
                    appendWhereCondition(sql, params, operands.get(i), entity);
                }
                sql.append("))");
            } else {
                // 回退到简单路径处理
                sql.append(leftQuotedIdentifier()).append(entityAttribute.columnName()).append(rightQuotedIdentifier());
                sql.append(" IN (");
                for (int i = 1; i < operands.size(); i++) {
                    if (i > 1) sql.append(", ");
                    appendWhereCondition(sql, params, operands.get(i), entity);
                }
                sql.append(")");
            }
        } else {
            throw new UnsupportedOperationException("Unsupported nested path: " + nestedPath);
        }
    }

    protected void appendPath(StringBuilder sql, PathNode path, EntityType entity) {
        EntityAttribute attribute = (EntityAttribute) entity.getAttribute(path);
        sql.append(leftQuotedIdentifier()).append(attribute.columnName()).append(rightQuotedIdentifier());
    }

    protected String leftQuotedIdentifier() {
        return sqlDialect.leftQuotedIdentifier();
    }

    protected String rightQuotedIdentifier() {
        return sqlDialect.rightQuotedIdentifier();
    }

    protected <R> R executeInTransaction(ConnectionCallback<R> action) {
        try {
            return connectionProvider.executeInTransaction(action);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected EntityType getEntityType() {
        return metamodel.getEntity(entityType);
    }

    protected String getColumnName(PathNode path) {
        EntityType entity = getEntityType();
        EntityAttribute attribute = (EntityAttribute) entity.getAttribute(path);
        return attribute.columnName();
    }
}