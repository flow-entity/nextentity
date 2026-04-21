package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.meta.MetamodelConfiguration;
import io.github.nextentity.core.meta.MetamodelResolver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// 默认元模型实现，管理实体类型元数据
public class DefaultMetamodel implements Metamodel {

    private final MetamodelResolver resolver;
    private final Map<Class<?>, DefaultEntitySchema> entityTypes = new ConcurrentHashMap<>();

    public static DefaultMetamodel of() {
        return new DefaultMetamodel(DefaultMetamodelConfiguration.DEFAULT);
    }

    public DefaultMetamodel(MetamodelConfiguration config) {
        this(DefaultMetamodelResolver.of(config));
    }

    public DefaultMetamodel(MetamodelResolver resolver) {
        this.resolver = resolver;
    }

    public EntityType getEntity(Class<?> type) {
        var entityType = getDefaultEntitySchema(type);
        entityType.getAttributes();
        return entityType;
    }

    DefaultEntitySchema getDefaultEntitySchema(Class<?> type) {
        return entityTypes.computeIfAbsent(type, this::createEntityType);
    }

    private DefaultEntitySchema createEntityType(Class<?> type) {
        return new DefaultEntitySchema(type, this);
    }

    protected MetamodelResolver getResolver() {
        return resolver;
    }
}