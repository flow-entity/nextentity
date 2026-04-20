package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ProjectionSchemaAttribute;
import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.reflect.LoadObserver;
import io.github.nextentity.core.reflect.LoadObserverRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/// 批量属性加载器。
///
/// 管理跨投影对象的批量加载，确保同一类型的目标实体只创建一个加载器。
/// 支持 WHERE IN 批量查询，避免 N+1 问题。
///
/// <p>工作流程：
/// <ol>
///   <li>通过 {@link #addForeignKey(Object)} 注册外键并获取 {@link AttributeLoader}</li>
///   <li>首次调用 {@code AttributeLoader.load()} 时触发批量查询</li>
///   <li>后续调用直接返回缓存结果</li>
/// </ol>
///
/// @author HuangChengwei
/// @since 2.1.0
public final class BatchAttributeLoader {

    private final QueryContext queryContext;
    private final ProjectionSchemaAttribute attribute;
    private final AttributeLoadFunction batchLoaderFunction;

    private final Set<Object> foreignKeys = ConcurrentHashMap.newKeySet();
    private final Map<Object, Object> cache = new HashMap<>();

    /// 根据属性类型自动选择加载策略。
    ///
    /// @param attribute 投影属性元数据
    /// @param queryContext 查询上下文
    public BatchAttributeLoader(ProjectionSchemaAttribute attribute, QueryContext queryContext) {
        this.attribute = attribute;
        this.queryContext = queryContext;
        if (attribute.type() == attribute.source().type()) {
            batchLoaderFunction = new EntityAttributeLoadFunction();
        } else {
            batchLoaderFunction = new ProjectionAttributeLoadFunction();
        }
    }

    /// 注册外键并返回对应的属性加载器。
    ///
    /// @param foreignKey 外键值
    /// @return 延迟加载器，首次 load 时触发批量查询
    public AttributeLoader addForeignKey(Object foreignKey) {
        foreignKeys.add(foreignKey);
        return () -> getValue(foreignKey);
    }

    private Object getValue(Object foreignKey) {
        if (cache.containsKey(foreignKey)) {
            Object cached = cache.get(foreignKey);
            notifyCacheHit(foreignKey, cached);
            return cached;
        }
        synchronized (cache) {
            if (!cache.containsKey(foreignKey)) {
                long startTime = System.currentTimeMillis();
                notifyBeforeLoad(startTime);
                Map<Object, Object> results = batchLoaderFunction.apply(this, foreignKeys);
                cache.putAll(results);
                for (Object key : foreignKeys) {
                    cache.putIfAbsent(key, null);
                }
                notifyAfterLoad(startTime, System.currentTimeMillis());
            }
        }
        return cache.get(foreignKey);
    }

    public QueryContext getQueryContext() {
        return queryContext;
    }

    public ProjectionSchemaAttribute getAttribute() {
        return attribute;
    }

    /// 通知缓存命中事件。
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

    /// 通知批量加载开始事件。
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

    /// 通知批量加载完成事件。
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