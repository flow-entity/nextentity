package io.github.nextentity.jpa;

import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.exception.OptimisticLockException;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class JpaUpdateExecutor implements UpdateExecutor {

    private final EntityManager entityManager;
    private final JpaTransactionTemplate transactionTemplate;
    private final Metamodel metamodel;

    public JpaUpdateExecutor(EntityManager entityManager, Metamodel metamodel) {
        this.entityManager = entityManager;
        this.transactionTemplate = DefaultTransactionTemplate.of();
        this.metamodel = metamodel;
    }

    public JpaUpdateExecutor(EntityManager entityManager, Metamodel metamodel, JpaTransactionTemplate jpaTransactionTemplate) {
        this.entityManager = entityManager;
        this.transactionTemplate = jpaTransactionTemplate;
        this.metamodel = metamodel;
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
        List<T> list = ImmutableList.ofIterable(entities);
        if (list.isEmpty()) {
            return list;
        }
        return doInTransaction(() -> {
            EntityType entity = metamodel.getEntity(entityType);
            String entityName = getJpaEntityName(entityType);
            EntityAttribute idAttribute = entity.id();
            EntityAttribute versionAttribute = entity.version();
            ImmutableArray<? extends Attribute> attributes = entity.attributes().getPrimitives();

            List<T> result = new ArrayList<>(list.size());
            for (T t : list) {
                if (entityManager.contains(t)) {
                    result.add(t);
                    continue;
                }
                Object id = idAttribute.get(t);
                Object version = versionAttribute != null ? versionAttribute.get(t) : null;

                StringBuilder jpql = new StringBuilder("UPDATE ")
                        .append(entityName).append(" e SET ");

                List<Object> params = new ArrayList<>();
                int paramIndex = 1;
                int attrCount = 0;

                for (var attr : attributes) {
                    String attrName = attr.name();
                    if (attrName.equals(idAttribute.name())) {
                        continue;
                    }
                    if (attrCount > 0) {
                        jpql.append(", ");
                    }
                    jpql.append("e.").append(attrName).append(" = ?").append(paramIndex);
                    Object value = versionAttribute == attr ? getNextVersion(t, versionAttribute) : attr.get(t);
                    params.add(value);
                    paramIndex++;
                    attrCount++;
                }

                jpql.append(" WHERE e.").append(idAttribute.name()).append(" = ?").append(paramIndex);
                params.add(id);
                paramIndex++;

                if (versionAttribute != null) {
                    jpql.append(" AND e.").append(versionAttribute.name()).append(" = ?").append(paramIndex);
                    params.add(version);
                    entityManager.detach(t);
                }

                Query query = entityManager.createQuery(jpql.toString());
                for (int i = 0; i < params.size(); i++) {
                    query.setParameter(i + 1, params.get(i));
                }

                int updated = query.executeUpdate();
                if (updated != 1) {
                    if (versionAttribute != null) {
                        throw new OptimisticLockException("Entity not found or concurrent modification detected for entity with id: " + id);
                    } else {
                        throw new IllegalStateException("Entity not found with id: " + id);
                    }
                }

                if (versionAttribute != null) {
                    incrementVersion(t, versionAttribute);
                }
                result.add(t);
            }
            return result;
        });
    }

    private <T> void incrementVersion(T entity, EntityAttribute versionAttribute) {
        Object version = getNextVersion(entity, versionAttribute);
        versionAttribute.set(entity, version);
    }

    private <T> Object getNextVersion(T entity, EntityAttribute versionAttribute) {
        Object version = versionAttribute.get(entity);
        Class<?> type = versionAttribute.type();
        if (type == Integer.class || type == int.class) {
            version = version == null ? 0 : (Integer) version + 1;
        } else if (type == Long.class || type == long.class) {
            version = version == null ? 0L : (Long) version + 1;
        } else {
            throw new IllegalStateException("Unsupported version type: " + type);
        }
        return version;
    }

    private String getJpaEntityName(Class<?> entityType) {
        var jpaEntityType = entityManager.getMetamodel().entity(entityType);
        return jpaEntityType.getName();
    }

    @Override
    public <T> void deleteAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityType) {
        List<T> list = ImmutableList.ofIterable(entities);
        if (list.isEmpty()) {
            return;
        }
        doInTransaction(() -> {
            EntityType entity = metamodel.getEntity(entityType);
            String entityName = getJpaEntityName(entityType);
            EntityAttribute idAttribute = entity.id();

            Set<Object> ids = new HashSet<>();
            for (T t : list) {
                if (!entityManager.contains(t)) {
                    Object id = idAttribute.get(t);
                    ids.add(id);
                } else {
                    entityManager.remove(t);
                }
            }

            String jpql = "DELETE FROM " + entityName + " e WHERE e." + idAttribute.name() + " IN (:ids)";
            Query query = entityManager.createQuery(jpql);
            query.setParameter("ids", ids);
            int updated = query.executeUpdate();
            if (updated != ids.size()) {
                throw new IllegalStateException("Deleted " + updated + " entities, expected " + list.size());
            }
            return null;
        });
    }

    @Override
    public <T> T doInTransaction(Supplier<T> command) {
        return transactionTemplate.executeInTransaction(entityManager, command);
    }
}
