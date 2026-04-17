package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.meta.MetamodelResolver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认元模型实现，管理实体类型元数据。
 * <p>
 * 复制自 AbstractMetamodel 的核心逻辑，后期将删除原类。
 */
public class DefaultMetamodel implements Metamodel {

    private final MetamodelResolver resolver;
    private final Map<Class<?>, DefaultEntitySchema> entityTypes = new ConcurrentHashMap<>();

    public DefaultMetamodel(MetamodelResolver resolver) {
        this.resolver = resolver;
    }

    public static DefaultMetamodel of() {
        return new DefaultMetamodel(DefaultMetamodelResolver.of());
    }

    public static DefaultMetamodel of(MetamodelResolver resolver) {
        return new DefaultMetamodel(resolver);
    }

    /**
     * 获取指定类的实体类型元数据，并缓存结果。
     *
     * @param type 实体类
     * @return 缓存的或新创建的实体类型元数据
     */
    public EntityType getEntity(Class<?> type) {
        return getDefaultEntitySchema(type);
    }

    DefaultEntitySchema getDefaultEntitySchema(Class<?> type) {
        return entityTypes.computeIfAbsent(type, this::createEntityType);
    }

    private DefaultEntitySchema createEntityType(Class<?> type) {
        return DefaultEntitySchema.of(type, this);
    }

    protected MetamodelResolver getResolver() {
        return resolver;
    }
}