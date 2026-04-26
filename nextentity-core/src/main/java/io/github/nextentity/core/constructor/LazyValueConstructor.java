package io.github.nextentity.core.constructor;

import io.github.nextentity.core.QueryConfig;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.JoinAttribute;
import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.reflect.LoadObserver;
import io.github.nextentity.core.reflect.LoadObserverRegistry;
import io.github.nextentity.core.util.NullableConcurrentMap;
import io.github.nextentity.jdbc.Arguments;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/// 懒加载值构造器
///
/// 负责创建 AttributeLoader，首次访问时触发批量加载。
/// 内部持有外键列和批量加载器，不需要 ArrayConstructor 包装外键。
///
/// 线程安全：使用 ConcurrentHashMap 收集外键，synchronized(cache) 保护批量加载过程，
/// 确保同一外键仅触发一次查询。
///
/// @author HuangChengwei
/// @since 2.2.2
public class LazyValueConstructor implements ValueConstructor {

    private final QueryConfig queryConfig;
    private final JoinAttribute attribute;
    private final LazyLoaderFunction batchLoaderFunction;

    private final Set<Object> foreignKeys = ConcurrentHashMap.newKeySet();
    private final Map<Object, Object> cache = new NullableConcurrentMap<>();
    private final List<SelectItem> columns;

    /// @param config    查询配置
    /// @param attribute 投影属性元数据
    /// @param column    外键列
    public LazyValueConstructor(QueryConfig config, JoinAttribute attribute, SelectItem column) {
        this.attribute = attribute;
        this.queryConfig = config;
        this.batchLoaderFunction = attribute.type() == attribute.getTargetEntityType().type()
                ? new EntityAttributeLoadFunction()
                : new ProjectionAttributeLoadFunction();
        this.columns = List.of(column);

    }

    /// 懒加载代理实现，首次调用 load() 时触发批量查询
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
                    LazyValueConstructor loader = LazyValueConstructor.this;
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

    public QueryConfig getQueryConfig() {
        return queryConfig;
    }

    public JoinAttribute getAttribute() {
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

    @Override
    public List<SelectItem> columns() {
        return columns;
    }

    /// 构造 AttributeLoader，收集外键供批量加载
    @Override
    public AttributeLoader construct(Arguments arguments) {
        EntityBasicAttribute targetAttribute = attribute.getTargetAttribute();
        Object foreignKey = arguments.next(targetAttribute.valueConvertor());
        foreignKeys.add(foreignKey);
        return new AttributeLoaderImpl(foreignKey);
    }
}