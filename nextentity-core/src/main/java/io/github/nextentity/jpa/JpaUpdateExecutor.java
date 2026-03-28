package io.github.nextentity.jpa;

import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.util.Iterators;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class JpaUpdateExecutor implements UpdateExecutor {

    private final EntityManager entityManager;
    private final PersistenceUnitUtil util;
    private final JpaTransactionTemplate transactionTemplate;

    public JpaUpdateExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        this.transactionTemplate = DefaultTransactionTemplate.of();
    }

    public JpaUpdateExecutor(EntityManager entityManager, JpaTransactionTemplate jpaTransactionTemplate) {
        this.entityManager = entityManager;
        this.util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        this.transactionTemplate = jpaTransactionTemplate;
    }

    @Override
    public <T> void insertAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityType) {
        doInTransaction(() -> {
            for (T entity : entities) {
                entityManager.persist(entity);
            }
        });
    }

    @Override
    public <T> List<T> updateAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityType) {
        return doInTransaction(() -> {
            List<T> list = new ArrayList<>(Iterators.size(entities));
            for (T entity : entities) {
                T merge = entityManager.merge(entity);
                list.add(merge);
            }
            return list;
        });
    }

    @Override
    public <T> void deleteAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityType) {
        doInTransaction(() -> {
            for (T entity : entities) {
                if (!entityManager.contains(entity)) {
                    entity = entityManager.merge(entity);
                }
                entityManager.remove(entity);
            }
        });
    }

    @Override
    public <T> T doInTransaction(Supplier<T> command) {
        return transactionTemplate.executeInTransaction(entityManager, command);
    }
}
