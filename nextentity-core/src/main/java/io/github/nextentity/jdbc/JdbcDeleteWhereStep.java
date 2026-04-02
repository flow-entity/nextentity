package io.github.nextentity.jdbc;

import io.github.nextentity.api.*;
import io.github.nextentity.api.Expression;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import org.jspecify.annotations.NonNull;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/// JDBC implementation of conditional delete builder.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 2.1
public class JdbcDeleteWhereStep<T> extends JdbcWhereStepSupport<T> implements DeleteWhereStep<T> {

    public JdbcDeleteWhereStep(Class<T> entityType,
                               Metamodel metamodel,
                               ConnectionProvider connectionProvider,
                               SqlDialect sqlDialect) {
        super(entityType, metamodel, connectionProvider, sqlDialect);
    }

    @Override
    public <N> ExpressionBuilder.PathOperator<T, N, ? extends DeleteWhereStep<T>> where(PathRef<T, N> path) {
        return new WhereOperator<>(PathNode.of(path));
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, ? extends DeleteWhereStep<T>> where(PathRef.NumberRef<T, N> path) {
        return new NumberOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public ExpressionBuilder.StringOperator<T, ? extends DeleteWhereStep<T>> where(PathRef.StringRef<T> path) {
        return new StringOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public DeleteWhereStep<T> where(@NonNull Expression<T, Boolean> predicate) {
        this.whereCondition = ExpressionNodes.getNode(predicate);
        return this;
    }

    private DeleteWhereStep<T> applyWhere(ExpressionNode condition) {
        setWhereCondition(condition);
        return this;
    }

    @Override
    public int execute() {
        EntityType entity = getEntityType();
        DeleteSqlStatement sql = buildDeleteSql(entity);

        return executeInTransaction(connection -> {
            sql.debug();
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement statement = connection.prepareStatement(sql.sql())) {
                JdbcUtil.setParameters(statement, sql.parameters());
                return statement.executeUpdate();
            }
        });
    }

    private DeleteSqlStatement buildDeleteSql(EntityType entity) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // Build DELETE FROM
        sql.append("DELETE FROM ");
        sql.append(leftQuotedIdentifier()).append(entity.tableName()).append(rightQuotedIdentifier());

        // Build WHERE
        if (whereCondition != null) {
            sql.append(" WHERE ");
            appendWhereCondition(sql, params, whereCondition, entity);
        }

        return new DeleteSqlStatement(sql.toString(), params);
    }

    private class WhereOperator<R> extends PathOperatorImpl<T, R, DeleteWhereStep<T>> {
        WhereOperator(ExpressionNode target) {
            super(target, JdbcDeleteWhereStep.this::applyWhere);
        }
    }
}