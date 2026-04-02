package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
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
                appendWhereCondition(sql, params, operands.get(0), entity);
                sql.append(" ").append(operator.sign()).append(" ");
                appendWhereCondition(sql, params, operands.get(1), entity);
            }
            case IN -> {
                appendWhereCondition(sql, params, operands.get(0), entity);
                sql.append(" IN (");
                for (int i = 1; i < operands.size(); i++) {
                    if (i > 1) sql.append(", ");
                    appendWhereCondition(sql, params, operands.get(i), entity);
                }
                sql.append(")");
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