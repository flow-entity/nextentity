package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Attributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class SimpleEntity implements EntityType {
    private final Map<Class<?>, ProjectionType> projections = new ConcurrentHashMap<>();
    private final Class<?> type;
    private final String tableName;
    private final BiFunction<EntityType, Class<?>, ProjectionType> projectionTypeGenerator;
    private Attributes attributes;
    private EntityAttribute id;
    private EntityAttribute version;

    public SimpleEntity(Class<?> type,
                        String tableName,
                        BiFunction<EntityType, Class<?>, ProjectionType> projectionTypeGenerator) {
        this.type = type;
        this.tableName = tableName;
        this.projectionTypeGenerator = projectionTypeGenerator;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
        EntityAttribute version = null;
        EntityAttribute id = null;
        for (Attribute attribute : attributes) {
            if (attribute instanceof EntityAttribute column) {
                if (column.isId()) {
                    id = column;
                } else if (column.isVersion()) {
                    version = column;
                }
            }
        }
        this.id = id;
        this.version = version;
    }

    @Override
    public ProjectionType getProjection(Class<?> type) {
        return projections.computeIfAbsent(type, this::generateProjectionType);
    }

    private ProjectionType generateProjectionType(Class<?> type) {
        return projectionTypeGenerator.apply(this, type);
    }

    @Override
    public EntityAttribute id() {
        return id;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public EntityAttribute version() {
        return version;
    }

    @Override
    public Attributes attributes() {
        return attributes;
    }

    @Override
    public Class<?> type() {
        return type;
    }
}
