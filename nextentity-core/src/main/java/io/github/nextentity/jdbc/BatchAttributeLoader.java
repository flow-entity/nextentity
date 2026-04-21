package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ProjectionSchemaAttribute;
import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.reflect.LoadObserver;
import io.github.nextentity.core.reflect.LoadObserverRegistry;
import io.github.nextentity.core.util.NullableConcurrentMap;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/// 批量属性加载器，支持 WHERE IN 批量查询避免 N+1 问题
public final class BatchAttributeLoader {

    private final QueryContext queryContext;
    private final ProjectionSchemaAttribute attribute;
    private final AttributeLoadFunction batchLoaderFunction;

    private final Set<Object> foreignKeys = ConcurrentHashMap.newKeySet();
    private final Map<Object, Object> cache = new NullableConcurrentMap<>();

    public BatchAttributeLoader(ProjectionSchemaAttribute attribute, QueryContext queryContext) {
        this.attribute = attribute;
        this.queryContext = queryContext;
        this.batchLoaderFunction = attribute.type() == attribute.source().type()
                ? new EntityAttributeLoadFunction()
                : new ProjectionAttributeLoadFunction();
    }

    public AttributeLoader getAttributeLoader(Object foreignKey) {
        foreignKeys.add(foreignKey);
        return new AttributeLoaderImpl(foreignKey);
    }

    private class AttributeLoaderImpl implements AttributeLoader {
        private final Object foreignKey;

        private AttributeLoaderImpl(Object foreignKey) {
            this.foreignKey = foreignKey;
        }

        @Override
        public Object load() {
            if (cache.containsKey(foreignKey)) {
                Object cached = cache.get(foreignKey);
                notifyCacheHit(foreignKey, cached);
                return cached;
            }
            synchronized (cache) {
                if (!cache.containsKey(foreignKey)) {
                    long startTime = System.currentTimeMillis();
                    notifyBeforeLoad(startTime);
                    BatchAttributeLoader loader = BatchAttributeLoader.this;
                    Map<Object, Object> results = batchLoaderFunction.apply(loader, foreignKeys);
                    cache.putAll(results);
                    for (Object key : foreignKeys) {
                        cache.putIfAbsent(key, null);
                    }
                    notifyAfterLoad(startTime, System.currentTimeMillis());
                }
            }
            return cache.get(foreignKey);
        }
    }

    public QueryContext getQueryContext() {
        return queryContext;
    }

    public ProjectionSchemaAttribute getAttribute() {
        return attribute;
    }

    private void notifyCacheHit(Object foreignKey, Object cached) {
        if (LoadObserverRegistry.isBound()) {
            LoadObserver obs = LoadObserverRegistry.get();
            obs.onCacheHit(new LoadObserver.CacheHitEvent(
                    getAttribute().type(),
                    foreignKey,
                    cached
            ));
        }
    }

    private void notifyBeforeLoad(long startTime) {
        if (LoadObserverRegistry.isBound()) {
            LoadObserver obs = LoadObserverRegistry.get();
            obs.onBeforeLoad(new LoadObserver.BatchLoadEvent(
                    getAttribute().type(),
                    foreignKeys,
                    startTime,
                    0
            ));
        }
    }

    private void notifyAfterLoad(long startTime, long endTime) {
        if (LoadObserverRegistry.isBound()) {
            LoadObserver obs = LoadObserverRegistry.get();
            obs.onAfterLoad(new LoadObserver.BatchLoadEvent(
                    getAttribute().type(),
                    foreignKeys,
                    startTime,
                    endTime
            ));
        }
    }
}