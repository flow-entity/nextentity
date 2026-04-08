package io.github.nextentity.jpa;

import io.github.nextentity.api.*;
import io.github.nextentity.api.Expression;
import io.github.nextentity.core.EntityContext;
import io.github.nextentity.core.expression.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/// JPA 条件更新构建器实现，使用 JPQL 构建 UPDATE 语句。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.0.0
public class JpaUpdateWhereStep<T> extends JpaWhereStepSupport<T> implements UpdateSetStep<T> {

    private final EntityManager entityManager;
    private final JpaTransactionTemplate transactionTemplate;
    private final List<SetValue> setValues = new ArrayList<>();

    public JpaUpdateWhereStep(EntityContext<T> context,
                              EntityManager entityManager,
                              JpaTransactionTemplate transactionTemplate) {
        super(context);
        this.entityManager = entityManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public <U> UpdateSetStep<T> set(PathRef<T, U> path, U value) {
        setValues.add(new SetValue(getAttributeName(PathNode.of(path)), value));
        return this;
    }

    public UpdateWhereStep<T> set(String fieldName, Object value) {
        throw new UnsupportedOperationException("Field name based set is not supported in JPA. Use PathRef instead.");
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

        return transactionTemplate.executeInTransaction(entityManager, () -> {
            String entityName = getJpaEntityName();
            StringBuilder jpql = new StringBuilder("UPDATE ")
                    .append(entityName).append(" e SET ");

            List<Object> params = new ArrayList<>();
            int paramIndex = 1;

            // Build SET clause
            String delimiter = "";
            for (SetValue setValue : setValues) {
                jpql.append(delimiter).append("e.").append(setValue.attributeName).append(" = ?").append(paramIndex);
                params.add(setValue.value);
                paramIndex++;
                delimiter = ", ";
            }

            // Build WHERE clause
            if (whereCondition != null) {
                jpql.append(" WHERE ");
                appendWhereCondition(jpql, params, whereCondition, paramIndex);
            }

            Query query = entityManager.createQuery(jpql.toString());
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i + 1, params.get(i));
            }

            return query.executeUpdate();
        });
    }

    private String getJpaEntityName() {
        return entityManager.getMetamodel().entity(getEntityClass()).getName();
    }

    private record SetValue(String attributeName, Object value) {}

    private class WhereOperator<R> extends PathOperatorImpl<T, R, UpdateWhereStep<T>> {
        WhereOperator(ExpressionNode target) {
            super(target, JpaUpdateWhereStep.this::applyWhere);
        }
    }
}