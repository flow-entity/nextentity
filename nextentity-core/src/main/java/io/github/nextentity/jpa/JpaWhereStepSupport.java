package io.github.nextentity.jpa;

import io.github.nextentity.core.EntityContext;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;

import java.util.List;

///
/// JPA 条件操作的基础类，提供通用的 JPQL 构建工具。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.0.0
abstract class JpaWhereStepSupport<T> {

    protected final EntityContext<T> context;
    protected ExpressionNode whereCondition;

    protected JpaWhereStepSupport(EntityContext<T> context) {
        this.context = context;
    }

    protected Class<T> getEntityClass() {
        return context.entityClass();
    }

    protected EntityType getEntityType() {
        return context.entityType();
    }

    protected void setWhereCondition(ExpressionNode condition) {
        if (this.whereCondition == null) {
            this.whereCondition = condition;
        } else {
            this.whereCondition = this.whereCondition.operate(Operator.AND, condition);
        }
    }

    protected int appendWhereCondition(StringBuilder jpql, List<Object> params,
                                        ExpressionNode node, int paramIndex) {
        if (node instanceof PathNode pathNode) {
            jpql.append("e.").append(getAttributeName(pathNode));
        } else if (node instanceof LiteralNode(Object value)) {
            jpql.append("?").append(paramIndex);
            params.add(value);
            return paramIndex + 1;
        } else if (node instanceof OperatorNode operatorNode) {
            return appendOperatorNode(jpql, params, operatorNode, paramIndex);
        }
        return paramIndex;
    }

    private int appendOperatorNode(StringBuilder jpql, List<Object> params,
                                    OperatorNode node, int paramIndex) {
        Operator operator = node.operator();
        List<? extends ExpressionNode> operands = node.operands();

        switch (operator) {
            case AND, OR -> {
                jpql.append("(");
                paramIndex = appendWhereCondition(jpql, params, operands.getFirst(), paramIndex);
                for (int i = 1; i < operands.size(); i++) {
                    jpql.append(" ").append(operator.sign()).append(" ");
                    paramIndex = appendWhereCondition(jpql, params, operands.get(i), paramIndex);
                }
                jpql.append(")");
            }
            case NOT -> {
                jpql.append("NOT ");
                paramIndex = appendWhereCondition(jpql, params, operands.getFirst(), paramIndex);
            }
            case EQ, NE, GT, GE, LT, LE, LIKE -> {
                paramIndex = appendWhereCondition(jpql, params, operands.get(0), paramIndex);
                jpql.append(" ").append(operator.sign()).append(" ");
                paramIndex = appendWhereCondition(jpql, params, operands.get(1), paramIndex);
            }
            case IN -> {
                paramIndex = appendWhereCondition(jpql, params, operands.getFirst(), paramIndex);
                jpql.append(" IN (");
                for (int i = 1; i < operands.size(); i++) {
                    if (i > 1) jpql.append(", ");
                    paramIndex = appendWhereCondition(jpql, params, operands.get(i), paramIndex);
                }
                jpql.append(")");
            }
            case IS_NULL -> {
                paramIndex = appendWhereCondition(jpql, params, operands.getFirst(), paramIndex);
                jpql.append(" IS NULL");
            }
            case IS_NOT_NULL -> {
                paramIndex = appendWhereCondition(jpql, params, operands.getFirst(), paramIndex);
                jpql.append(" IS NOT NULL");
            }
            case BETWEEN -> {
                paramIndex = appendWhereCondition(jpql, params, operands.get(0), paramIndex);
                jpql.append(" BETWEEN ");
                paramIndex = appendWhereCondition(jpql, params, operands.get(1), paramIndex);
                jpql.append(" AND ");
                paramIndex = appendWhereCondition(jpql, params, operands.get(2), paramIndex);
            }
            default -> throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
        return paramIndex;
    }

    protected String getAttributeName(PathNode pathNode) {
        // 对于嵌套路径，生成完整的路径表达式（如 department.name）
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pathNode.size(); i++) {
            if (i > 0) {
                sb.append(".");
            }
            sb.append(pathNode.get(i));
        }
        return sb.toString();
    }
}