package io.github.nextentity.jpa;

import io.github.nextentity.api.*;
import io.github.nextentity.api.Expression;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.Metamodel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/// JPA 条件删除构建器实现，使用 JPQL 构建 DELETE 语句。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.0.0
public class JpaDeleteWhereStep<T> extends JpaWhereStepSupport<T> implements DeleteWhereStep<T> {

    private final EntityManager entityManager;
    private final JpaTransactionTemplate transactionTemplate;

    public JpaDeleteWhereStep(Class<T> entityClass,
                              Metamodel metamodel,
                              EntityManager entityManager,
                              JpaTransactionTemplate transactionTemplate) {
        super(entityClass, metamodel);
        this.entityManager = entityManager;
        this.transactionTemplate = transactionTemplate;
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
        return transactionTemplate.executeInTransaction(entityManager, () -> {
            String entityName = getJpaEntityName();
            StringBuilder jpql = new StringBuilder("DELETE FROM ")
                    .append(entityName).append(" e");

            List<Object> params = new ArrayList<>();

            // Build WHERE clause
            if (whereCondition != null) {
                jpql.append(" WHERE ");
                appendWhereCondition(jpql, params, whereCondition, 1);
            }

            Query query = entityManager.createQuery(jpql.toString());
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i + 1, params.get(i));
            }

            return query.executeUpdate();
        });
    }

    private String getJpaEntityName() {
        return entityManager.getMetamodel().entity(entityClass).getName();
    }

    private class WhereOperator<R> extends PathOperatorImpl<T, R, DeleteWhereStep<T>> {
        WhereOperator(ExpressionNode target) {
            super(target, JpaDeleteWhereStep.this::applyWhere);
        }
    }
}