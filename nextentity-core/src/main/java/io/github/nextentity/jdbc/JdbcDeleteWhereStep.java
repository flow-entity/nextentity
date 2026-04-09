package io.github.nextentity.jdbc;

import io.github.nextentity.api.*;
import io.github.nextentity.api.Expression;
import io.github.nextentity.api.EntityDescriptor;
import io.github.nextentity.core.expression.*;
import org.jspecify.annotations.NonNull;

import java.sql.PreparedStatement;

/// JDBC 条件删除构建器实现。
///
/// 使用原生 SQL 构建 DELETE 语句，支持带 WHERE 条件的批量删除。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.0.0
public class JdbcDeleteWhereStep<T> extends JdbcWhereStepSupport<T> implements DeleteWhereStep<T> {

    private final JdbcUpdateSqlBuilder sqlBuilder;

    public JdbcDeleteWhereStep(EntityDescriptor<T> descriptor,
                               ConnectionProvider connectionProvider,
                               JdbcUpdateSqlBuilder sqlBuilder) {
        super(descriptor, connectionProvider);
        this.sqlBuilder = sqlBuilder;
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
    public <N> ExpressionBuilder.PathOperator<T, N, ? extends DeleteWhereStep<T>> where(Path<T, N> path) {
        return new WhereOperator<>(ExpressionNodes.getNode(path));
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, ? extends DeleteWhereStep<T>> where(NumberPath<T, N> path) {
        return new NumberOperatorImpl<>(ExpressionNodes.getNode(path), this::applyWhere);
    }

    @Override
    public ExpressionBuilder.StringOperator<T, ? extends DeleteWhereStep<T>> where(StringPath<T> path) {
        return new StringOperatorImpl<>(ExpressionNodes.getNode(path), this::applyWhere);
    }

    @Override
    public <R extends Entity> ExpressionBuilder.PathOperator<T, R, ? extends DeleteWhereStep<T>> where(PathRef.EntityPathRef<T, R> path) {
        return new WhereOperator<>(ExpressionNodes.getNode(path));
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
        DeleteSqlStatement sql = sqlBuilder.buildConditionalDeleteStatement(
                getEntityType(), getMetamodel(), whereCondition);

        return executeInTransaction(connection -> {
            sql.debug();
            try (PreparedStatement statement = connection.prepareStatement(sql.sql())) {
                JdbcUtil.setParameters(statement, sql.parameters());
                return statement.executeUpdate();
            }
        });
    }

    private class WhereOperator<R> extends PathOperatorImpl<T, R, DeleteWhereStep<T>> {
        WhereOperator(ExpressionNode target) {
            super(target, JdbcDeleteWhereStep.this::applyWhere);
        }
    }
}