package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.jdbc.ConnectionProvider.ConnectionCallback;

import java.sql.SQLException;
import java.util.List;

/// JDBC 条件操作基类。
///
/// 提供 WHERE 步骤实现的公共基础设施。
///
/// @param <T> 实体类型
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