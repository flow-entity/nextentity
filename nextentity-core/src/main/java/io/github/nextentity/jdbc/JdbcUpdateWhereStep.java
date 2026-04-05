package io.github.nextentity.jdbc;

import io.github.nextentity.api.*;
import io.github.nextentity.api.Expression;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.Metamodel;
import org.jspecify.annotations.NonNull;

import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.Map;

/// JDBC 条件更新构建器实现。
///
/// 使用原生 SQL 构建 UPDATE 语句，支持带 WHERE 条件的批量更新。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.0.0
public class JdbcUpdateWhereStep<T> extends JdbcWhereStepSupport<T> implements UpdateSetStep<T> {

    private final Map<String, Object> setValues = new LinkedHashMap<>();
    private final JdbcUpdateSqlBuilder sqlBuilder;

    public JdbcUpdateWhereStep(Class<T> entityType,
                               Metamodel metamodel,
                               ConnectionProvider connectionProvider,
                               JdbcUpdateSqlBuilder sqlBuilder) {
        super(entityType, metamodel, connectionProvider);
        this.sqlBuilder = sqlBuilder;
    }

    @Override
    public <U> UpdateSetStep<T> set(PathRef<T, U> path, U value) {
        String columnName = getColumnName(PathNode.of(path));
        setValues.put(columnName, value);
        return this;
    }

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
    public <N> ExpressionBuilder.PathOperator<T, N, ? extends UpdateWhereStep<T>> where(Path<T, N> path) {
        return new WhereOperator<>(ExpressionNodes.getNode(path));
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, ? extends UpdateWhereStep<T>> where(NumberPath<T, N> path) {
        return new NumberOperatorImpl<>(ExpressionNodes.getNode(path), this::applyWhere);
    }

    @Override
    public ExpressionBuilder.StringOperator<T, ? extends UpdateWhereStep<T>> where(StringPath<T> path) {
        return new StringOperatorImpl<>(ExpressionNodes.getNode(path), this::applyWhere);
    }

    @Override
    public <R extends Entity> ExpressionBuilder.PathOperator<T, R, ? extends UpdateWhereStep<T>> where(PathRef.EntityPathRef<T, R> path) {
        return new WhereOperator<>(ExpressionNodes.getNode(path));
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

        UpdateSqlStatement sql = sqlBuilder.buildConditionalUpdateStatement(
                getEntityType(), metamodel, setValues, whereCondition);

        return executeInTransaction(connection -> {
            sql.debug();
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement statement = connection.prepareStatement(sql.sql())) {
                JdbcUtil.setParameters(statement, sql.parameters());
                return statement.executeUpdate();
            }
        });
    }

    private class WhereOperator<R> extends PathOperatorImpl<T, R, UpdateWhereStep<T>> {
        WhereOperator(ExpressionNode target) {
            super(target, JdbcUpdateWhereStep.this::applyWhere);
        }
    }
}