package io.github.nextentity.jpa;

import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.util.Iterators;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceUnitUtil;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class JpaUpdateExecutor implements UpdateExecutor {

    private final EntityManager entityManager;
    private final PersistenceUnitUtil util;

    public JpaUpdateExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
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
                entityManager.remove(entity);
            }
        });
    }

    @Override
    public <T> T patch(@NonNull T entity, @NonNull Class<T> entityType) {
        return doInTransaction(() -> {
            Object id = requireId(entity);
            T t = entityManager.find(entityType, id);
            if (t == null) {
                throw new IllegalArgumentException("id not found");
            }
            ReflectUtil.copyTargetNullFields(t, entity, entityType);
            return entityManager.merge(entity);
        });
    }

    private <T> Object requireId(T entity) {
        Object id = util.getIdentifier(entity);
        return Objects.requireNonNull(id);
    }

    @Override
    public <T> T doInTransaction(Supplier<T> command) {
        EntityTransaction transaction = entityManager.getTransaction();
        if (transaction.isActive()) {
            return command.get();
        }
        transaction.begin();
        try {
            T result = command.get();
            transaction.commit();
            return result;
        } catch (Throwable e) {
            transaction.rollback();
            throw e;
        }
    }
}
