package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.core.util.Iterators;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractUpdateSqlBuilder implements JdbcUpdateSqlBuilder {

    @Override
    public List<InsertSqlStatement> buildInsertStatement(Iterable<?> entities, @NotNull EntityType entityType) {
        EntityAttribute idAttribute = entityType.id();
        boolean hasNullId = false;
        for (Object entity : entities) {
            Object id = idAttribute.getDatabaseValue(entity);
            if (id == null) {
                hasNullId = true;
            }
        }
        ImmutableArray<? extends EntityAttribute> selectList = entityType.primitiveAttributes();
        return Collections.singletonList(buildInsertStatement(entities, entityType, selectList, hasNullId));
    }

    protected InsertSqlStatement buildInsertStatement(Iterable<?> entities,
                                                      EntityType entityType,
                                                      Iterable<? extends EntityAttribute> attributes,
                                                      boolean generateKey) {
        String tableName = entityType.tableName();
        List<EntityAttribute> columns = new ArrayList<>();
        StringBuilder sql = new StringBuilder("insert into ")
                .append(leftTicks())
                .append(tableName)
                .append(rightTicks())
                .append(" (");
        String delimiter = "";
        for (EntityAttribute attribute : attributes) {
            sql.append(delimiter).append(leftTicks()).append(attribute.columnName()).append(rightTicks());
            columns.add(attribute);
            delimiter = ",";
        }
        sql.append(") values (");
        delimiter = "";
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            sql.append(delimiter).append("?");
            delimiter = ",";
        }
        sql.append(")");
        Iterable<? extends Iterable<?>> parameters = getParameters(entities, attributes);
        return new InsertSqlStatement(entities, sql.toString(), parameters, generateKey);
    }

    private static Iterable<? extends Iterable<?>> getParameters(Iterable<?> entities,
                                                                 Iterable<? extends EntityAttribute> attributes) {
        return Iterators.map(entities, entity -> Iterators.map(attributes, attr -> {
            Object value = attr.getDatabaseValue(entity);
            return value == null ? new NullParameter(attr.type()) : value;
        }));
    }

    @NotNull
    protected abstract String rightTicks();

    @NotNull
    protected abstract String leftTicks();

    @Override
    public BatchSqlStatement buildUpdateStatement(Iterable<?> entities,
                                                  EntitySchema entityType,
                                                  boolean excludeNull) {
        ImmutableArray<? extends EntityAttribute> columns = entityType.primitiveAttributes();
        StringBuilder sql = new StringBuilder("update ")
                .append(leftTicks())
                .append(entityType.tableName())
                .append(rightTicks())
                .append(" set ");
        EntityAttribute id = entityType.id();
        String delimiter = "";
        List<EntityAttribute> paramAttr = new ArrayList<>(columns.size() + 1);
        EntityAttribute version = entityType.version();
        for (EntityAttribute attribute : columns) {
            if (entityType.id() == attribute) {
                continue;
            }
            sql.append(delimiter);
            delimiter = ",";
            sql.append(leftTicks()).append(attribute.columnName()).append(rightTicks()).append("=");
            if (excludeNull) {
                sql.append("case when ").append(typedPlaceholder(attribute)).append(" is null then")
                        .append(leftTicks()).append(attribute.columnName()).append(rightTicks())
                        .append("else ").append(typedPlaceholder(attribute))
                        .append(" end");
                paramAttr.add(attribute);
                paramAttr.add(attribute);
            } else {
                if (attribute == version) {
                    sql.append("?+1");
                } else {
                    sql.append("?");
                }
                paramAttr.add(attribute);
            }
        }
        sql.append(" where ").append(leftTicks()).append(id.columnName()).append(rightTicks()).append("=?");
        paramAttr.add(id);
        if (version != null) {
            sql.append(" and ")
                    .append(leftTicks())
                    .append(version.columnName())
                    .append(rightTicks())
                    .append("=?");
            paramAttr.add(version);
        }
        return new BatchSqlStatement(sql.toString(), getParameters(entities, paramAttr));
    }

    protected @NotNull String typedPlaceholder(EntityAttribute attribute) {
        return "?";
    }

    @Override
    public BatchSqlStatement buildDeleteStatement(Iterable<?> entities, EntityType entity) {
        EntityAttribute id = entity.id();
        String sql = "delete from " + leftTicks() + entity.tableName() + rightTicks()
                     + " where " + leftTicks() + id.columnName() + rightTicks() + "=?";
        List<EntityAttribute> paramAttr = Collections.singletonList(id);
        Iterable<? extends Iterable<?>> parameters = getParameters(entities, paramAttr);
        return new BatchSqlStatement(sql, parameters);
    }

    protected List<InsertSqlStatement> buildGroupedInsertStatement(Iterable<?> entities, @NotNull EntityType entityType) {
        EntityAttribute idAttribute = entityType.id();
        ImmutableArray<? extends EntityAttribute> basicAttributes = entityType.primitiveAttributes();
        List<? extends EntityAttribute> withoutId = basicAttributes.stream()
                .filter(attr -> attr != idAttribute)
                .collect(ImmutableList.collector(basicAttributes.size() - 1));
        List<InsertSqlStatement> result = new ArrayList<>();
        List<Object> entitiesHasId = new ArrayList<>();
        for (Object entity : entities) {
            List<?> single = Collections.singletonList(entity);
            if (idAttribute.get(entity) == null) {
                result.add(buildInsertStatement(single, entityType, withoutId, true));
            } else {
                entitiesHasId.add(entity);
            }
        }
        if (!entitiesHasId.isEmpty()) {
            result.add(buildInsertStatement(entitiesHasId, entityType, basicAttributes, false));
        }
        return result;
    }

}
