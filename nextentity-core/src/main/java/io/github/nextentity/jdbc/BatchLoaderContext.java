package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ProjectionSchemaAttribute;
import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.reflect.BatchAttributeLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/// 批量加载上下文
///
/// 管理跨投影对象的批量加载器，确保同一类型的目标实体只创建一个批量加载器。
/// 支持 WHERE IN 批量查询，避免 N+1 问题。
///
/// @author HuangChengwei
/// @since 2.1.0
public final class BatchLoaderContext implements BatchAttributeLoader {

    private final QueryContext queryContext;
    private final ProjectionSchemaAttribute attribute;

    private final Map<Object, AttributeLoader> attributeLoaders = new ConcurrentHashMap<>();
    private final Map<Object, Object> cache = new HashMap<>();
    private boolean loaded = false;

    public BatchLoaderContext(ProjectionSchemaAttribute attribute, QueryContext queryContext) {
        this.attribute = attribute;
        this.queryContext = queryContext;
    }

    @Override
    public AttributeLoader addForeignKey(Object loader) {
        return attributeLoaders.computeIfAbsent(loader, this::getEntityLoader);
    }

    private AttributeLoader getEntityLoader(Object k) {
        if (attribute.type() == attribute.source().type()) {
            return new EntityAttributeLoader(this, k);
        } else {
            return new ProjectionAttributeLoader(this, k);
        }
    }

    public QueryContext getQueryContext() {
        return queryContext;
    }

    public ProjectionSchemaAttribute getAttribute() {
        return attribute;
    }

    public Set<Object> getForeignKeys() {
        return attributeLoaders.keySet();
    }

    public Map<Object, Object> getCache() {
        return cache;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}