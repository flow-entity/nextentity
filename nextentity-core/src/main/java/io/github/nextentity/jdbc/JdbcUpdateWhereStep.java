package io.github.nextentity.jdbc;

import io.github.nextentity.api.*;
import io.github.nextentity.api.Expression;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import org.jspecify.annotations.NonNull;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/// JDBC 条件更新构建器实现。
///
/// 使用原生 SQL 构建 UPDATE 语句，支持带 WHERE 条件的批量更新。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1
public class JdbcUpdateWhereStep<T> extends JdbcWhereStepSupport<T> implements UpdateWhereStep<T> {

    private final UpdateExecutor updateExecutor;
    private final Map<String, Object> setValues = new LinkedHashMap<>();

    public JdbcUpdateWhereStep(Class<T> entityType,
                               Metamodel metamodel,
                               UpdateExecutor updateExecutor,
                               ConnectionProvider connectionProvider,
                               SqlDialect sqlDialect) {
        super(entityType, metamodel, connectionProvider, sqlDialect);
        this.updateExecutor = updateExecutor;
    }

    @Override
    public <U> UpdateWhereStep<T> set(PathRef<T, U> path, U value) {
        String columnName = getColumnName(PathNode.of(path));
        setValues.put(columnName, value);
        return this;
    }

    @Override
    public UpdateWhereStep<T> set(String fieldName, Object value) {
        setValues.put(fieldName, value);
        return this;
    }

    @Override
    public <N> ExpressionBuilder.PathOperator<T, N, ? extends UpdateWhereStep<T>> where(PathRef<T, N> path) {
        return new WhereOperator<>(PathNode.of(path));
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, ? extends UpdateWhereStep<T>> where(PathRef.NumberRef<T, N> path) {
        return new NumberOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public ExpressionBuilder.StringOperator<T, ? extends UpdateWhereStep<T>> where(PathRef.StringRef<T> path) {
        return new StringOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public UpdateWhereStep<T> where(@NonNull Expression<T, Boolean> predicate) {
        this.whereCondition = ExpressionNodes.getNode(predicate);
        return this;
    }

    private UpdateWhereStep<T> applyWhere(ExpressionNode condition) {
        setWhereCondition(condition);
        return this;
    }

    @Override
    public int execute() {
        if (setValues.isEmpty()) {
            throw new IllegalStateException("No SET values specified for update");
        }

        EntityType entity = getEntityType();
        UpdateSqlStatement sql = buildUpdateSql(entity);

        return executeInTransaction(connection -> {
            sql.debug();
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement statement = connection.prepareStatement(sql.sql())) {
                JdbcUtil.setParameters(statement, sql.parameters());
                return statement.executeUpdate();
            }
        });
    }

    private UpdateSqlStatement buildUpdateSql(EntityType entity) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // Build UPDATE ... SET
        sql.append("UPDATE ");
        sql.append(leftQuotedIdentifier()).append(entity.tableName()).append(rightQuotedIdentifier());
        sql.append(" SET ");

        String delimiter = "";
        for (Map.Entry<String, Object> entry : setValues.entrySet()) {
            sql.append(delimiter);
            sql.append(leftQuotedIdentifier()).append(entry.getKey()).append(rightQuotedIdentifier());
            sql.append(" = ?");
            params.add(entry.getValue());
            delimiter = ", ";
        }

        // Build WHERE
        if (whereCondition != null) {
            sql.append(" WHERE ");
            appendWhereCondition(sql, params, whereCondition, entity);
        }

        return new UpdateSqlStatement(sql.toString(), params);
    }

    private class WhereOperator<R> extends PathOperatorImpl<T, R, UpdateWhereStep<T>> {
        WhereOperator(ExpressionNode target) {
            super(target, JdbcUpdateWhereStep.this::applyWhere);
        }
    }
}