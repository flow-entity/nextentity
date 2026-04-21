package io.github.nextentity.jpa;

import io.github.nextentity.core.PersistDescriptor;
import io.github.nextentity.core.PersistExecutor;
import io.github.nextentity.core.exception.OptimisticLockException;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.stream.Collectors;

///
/// JPA 更新执行器，负责执行插入、更新和删除操作。
/// 该执行器通过 JPA EntityManager 处理批量实体操作，并支持乐观锁机制。
///
/// @author HuangChengwei
/// @since 2.0.0
public class JpaPersistExecutor implements PersistExecutor {

    private final EntityManager entityManager;

    public JpaPersistExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <T> void insertAll(@NonNull Iterable<T> entities, @NonNull PersistDescriptor<T> descriptor) {
        for (T entity : entities) {
            entityManager.persist(entity);
        }
    }

    @Override
    public <T> void updateAll(@NonNull Iterable<T> entities, @NonNull PersistDescriptor<T> descriptor) {
        List<T> list = ImmutableList.ofIterable(entities);
        if (list.isEmpty()) {
            return;
        }
        Class<T> entityType = descriptor.entityClass();
        EntityType entity = descriptor.entityType();
        String entityName = getJpaEntityName(entityType);
        EntityAttribute idAttribute = entity.id();
        EntityAttribute versionAttribute = entity.version();
        ImmutableArray<? extends Attribute> attributes = entity.getPrimitives();

        for (T t : list) {
            if (entityManager.contains(t)) {
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
        }

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
    public <T> void deleteAll(@NonNull Iterable<T> entities, @NonNull PersistDescriptor<T> descriptor) {
        List<T> list = ImmutableList.ofIterable(entities);
        if (list.isEmpty()) {
            return;
        }
        Class<T> entityClass = descriptor.entityClass();
        EntityType entity = descriptor.entityType();
        String entityName = getJpaEntityName(entityClass);
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

    }

    @Override
    public <T> int update(UpdateStructure structure, @NonNull PersistDescriptor<T> descriptor) {
        Map<String, Object> setValues = structure.setClauses();
        if (setValues.isEmpty()) {
            throw new IllegalStateException("No SET values specified for update");
        }

        String entityName = getJpaEntityName(descriptor.entityClass());
        StringBuilder jpql = new StringBuilder("UPDATE ")
                .append(entityName).append(" e SET ");

        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        // Build SET clause
        String delimiter = "";
        for (Map.Entry<String, Object> setValue : setValues.entrySet()) {
            jpql.append(delimiter).append("e.").append(setValue.getKey()).append(" = ?").append(paramIndex);
            params.add(setValue.getValue());
            paramIndex++;
            delimiter = ", ";
        }
        ExpressionNode whereCondition = structure.where();
        // Build WHERE clause
        if (whereCondition != null && !(whereCondition instanceof EmptyNode)) {
            jpql.append(" WHERE ");
            appendWhereCondition(jpql, params, whereCondition, paramIndex);
        }

        Query query = entityManager.createQuery(jpql.toString());
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        return query.executeUpdate();
    }

    @Override
    public <T> int delete(ExpressionNode predicate, @NonNull PersistDescriptor<T> descriptor) {
        String entityName = getJpaEntityName(descriptor.entityClass());
        StringBuilder jpql = new StringBuilder("DELETE FROM ")
                .append(entityName).append(" e");

        List<Object> params = new ArrayList<>();

        // Build WHERE clause
        if (predicate != null && !(predicate instanceof EmptyNode)) {
            jpql.append(" WHERE ");
            appendWhereCondition(jpql, params, predicate, 1);
        }

        Query query = entityManager.createQuery(jpql.toString());
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        return query.executeUpdate();
    }


    protected int appendWhereCondition(StringBuilder jpql, List<Object> params,
                                       ExpressionNode node, int paramIndex) {
        if (node instanceof PathNode pathNode) {
            jpql.append("e.").append(getAttributeName(pathNode));
        } else if (node instanceof LiteralNode(Object value)) {
            jpql.append("?").append(paramIndex);
            params.add(value);
            return paramIndex + 1;
        } else if (node instanceof OperatorNode operatorNode) {
            return appendOperatorNode(jpql, params, operatorNode, paramIndex);
        }
        return paramIndex;
    }

    private int appendOperatorNode(StringBuilder jpql, List<Object> params,
                                   OperatorNode node, int paramIndex) {
        Operator operator = node.operator();
        List<? extends ExpressionNode> operands = node.operands();

        switch (operator) {
            case AND, OR -> {
                jpql.append("(");
                paramIndex = appendWhereCondition(jpql, params, operands.getFirst(), paramIndex);
                for (int i = 1; i < operands.size(); i++) {
                    jpql.append(" ").append(operator.sign()).append(" ");
                    paramIndex = appendWhereCondition(jpql, params, operands.get(i), paramIndex);
                }
                jpql.append(")");
            }
            case NOT -> {
                jpql.append("NOT ");
                paramIndex = appendWhereCondition(jpql, params, operands.getFirst(), paramIndex);
            }
            case EQ, NE, GT, GE, LT, LE, LIKE -> {
                paramIndex = appendWhereCondition(jpql, params, operands.get(0), paramIndex);
                jpql.append(" ").append(operator.sign()).append(" ");
                paramIndex = appendWhereCondition(jpql, params, operands.get(1), paramIndex);
            }
            case IN -> {
                paramIndex = appendWhereCondition(jpql, params, operands.getFirst(), paramIndex);
                jpql.append(" IN (");
                for (int i = 1; i < operands.size(); i++) {
                    if (i > 1) jpql.append(", ");
                    paramIndex = appendWhereCondition(jpql, params, operands.get(i), paramIndex);
                }
                jpql.append(")");
            }
            case IS_NULL -> {
                paramIndex = appendWhereCondition(jpql, params, operands.getFirst(), paramIndex);
                jpql.append(" IS NULL");
            }
            case IS_NOT_NULL -> {
                paramIndex = appendWhereCondition(jpql, params, operands.getFirst(), paramIndex);
                jpql.append(" IS NOT NULL");
            }
            case BETWEEN -> {
                paramIndex = appendWhereCondition(jpql, params, operands.get(0), paramIndex);
                jpql.append(" BETWEEN ");
                paramIndex = appendWhereCondition(jpql, params, operands.get(1), paramIndex);
                jpql.append(" AND ");
                paramIndex = appendWhereCondition(jpql, params, operands.get(2), paramIndex);
            }
            default -> throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
        return paramIndex;
    }

    protected String getAttributeName(PathNode pathNode) {
        return pathNode.stream().collect(Collectors.joining("."));
    }

}
