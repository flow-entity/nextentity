package io.github.nextentity.jdbc;

import io.github.nextentity.core.EntityContext;
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
/// @since 2.0.0
abstract class JdbcWhereStepSupport<T> {

    protected final EntityContext<T> context;
    protected final ConnectionProvider connectionProvider;
    protected ExpressionNode whereCondition;

    protected JdbcWhereStepSupport(EntityContext<T> context,
                                   ConnectionProvider connectionProvider) {
        this.context = context;
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
        return context.entityType();
    }

    protected Metamodel getMetamodel() {
        return context.metamodel();
    }

    protected String getColumnName(PathNode path) {
        EntityType entity = getEntityType();
        EntityAttribute attribute = (EntityAttribute) entity.getAttribute(path);
        return attribute.columnName();
    }

}