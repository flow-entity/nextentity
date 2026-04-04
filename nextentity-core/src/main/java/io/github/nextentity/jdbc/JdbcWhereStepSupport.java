package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.jdbc.ConnectionProvider.ConnectionCallback;

import java.sql.SQLException;
import java.util.List;

/// Base class for JDBC conditional operations.
///
/// Provides common infrastructure for WHERE step implementations.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 2.1
abstract class JdbcWhereStepSupport<T> {

    protected final Class<T> entityType;
    protected final Metamodel metamodel;
    protected final ConnectionProvider connectionProvider;
    protected ExpressionNode whereCondition;

    protected JdbcWhereStepSupport(Class<T> entityType,
                                   Metamodel metamodel,
                                   ConnectionProvider connectionProvider) {
        this.entityType = entityType;
        this.metamodel = metamodel;
        this.connectionProvider = connectionProvider;
    }

    protected void setWhereCondition(ExpressionNode condition) {
        if (this.whereCondition == null) {
            this.whereCondition = condition;
        } else {
            this.whereCondition = this.whereCondition.operate(Operator.AND, condition);
        }
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