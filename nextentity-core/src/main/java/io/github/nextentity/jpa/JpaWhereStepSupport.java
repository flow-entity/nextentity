package io.github.nextentity.jpa;

import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

import java.util.List;

/// Base class for JPA conditional operations with common JPQL building utilities.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 2.1
abstract class JpaWhereStepSupport<T> {

    protected final Class<T> entityClass;
    protected final Metamodel metamodel;
    protected ExpressionNode whereCondition;

    protected JpaWhereStepSupport(Class<T> entityClass, Metamodel metamodel) {
        this.entityClass = entityClass;
        this.metamodel = metamodel;
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
        } else if (node instanceof LiteralNode literalNode) {
            jpql.append("?").append(paramIndex);
            params.add(literalNode.value());
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
                paramIndex = appendWhereCondition(jpql, params, operands.get(0), paramIndex);
                for (int i = 1; i < operands.size(); i++) {
                    jpql.append(" ").append(operator.sign()).append(" ");
                    paramIndex = appendWhereCondition(jpql, params, operands.get(i), paramIndex);
                }
                jpql.append(")");
            }
            case NOT -> {
                jpql.append("NOT ");
                paramIndex = appendWhereCondition(jpql, params, operands.get(0), paramIndex);
            }
            case EQ, NE, GT, GE, LT, LE, LIKE -> {
                paramIndex = appendWhereCondition(jpql, params, operands.get(0), paramIndex);
                jpql.append(" ").append(operator.sign()).append(" ");
                paramIndex = appendWhereCondition(jpql, params, operands.get(1), paramIndex);
            }
            case IN -> {
                paramIndex = appendWhereCondition(jpql, params, operands.get(0), paramIndex);
                jpql.append(" IN (");
                for (int i = 1; i < operands.size(); i++) {
                    if (i > 1) jpql.append(", ");
                    paramIndex = appendWhereCondition(jpql, params, operands.get(i), paramIndex);
                }
                jpql.append(")");
            }
            case IS_NULL -> {
                paramIndex = appendWhereCondition(jpql, params, operands.get(0), paramIndex);
                jpql.append(" IS NULL");
            }
            case IS_NOT_NULL -> {
                paramIndex = appendWhereCondition(jpql, params, operands.get(0), paramIndex);
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
        EntityType entity = metamodel.getEntity(entityClass);
        EntityAttribute attribute = (EntityAttribute) entity.getAttribute(pathNode);
        return attribute.name();
    }
}