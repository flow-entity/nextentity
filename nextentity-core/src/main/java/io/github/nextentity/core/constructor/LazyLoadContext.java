package io.github.nextentity.core.constructor;

import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.reflect.LoadObserver;
import io.github.nextentity.core.reflect.LoadObserverRegistry;
import io.github.nextentity.core.util.NullableConcurrentMap;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/// 懒加载上下文
///
/// 管理批量加载的外键集合和缓存，使用 ValueConstructor 构造结果。
/// 替代原有的 BatchAttributeLoader，但简化构造逻辑。
///
/// @author HuangChengwei
/// @since 2.2.2
public class LazyLoadContext {

    private final ValueConstructor resultConstructor;
    private final LazyLoadFunction loadFunction;

    private final Set<Object> foreignKeys = ConcurrentHashMap.newKeySet();
    private final Map<Object, Object> cache = new NullableConcurrentMap<>();

    public LazyLoadContext(ValueConstructor resultConstructor, LazyLoadFunction loadFunction) {
        this.resultConstructor = resultConstructor;
        this.loadFunction = loadFunction;
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
            Object cached = cache.get(foreignKey);
            if (cached != null) {
                notifyCacheHit(foreignKey, cached);
                return cached;
            }
            synchronized (cache) {
                cached = cache.get(foreignKey);
                if (cached == null) {
                    long startTime = System.currentTimeMillis();
                    notifyBeforeLoad(startTime);

                    Map<Object, Object> results = loadFunction.apply(resultConstructor, foreignKeys);
                    cache.putAll(results);

                    for (Object key : foreignKeys) {
                        cache.putIfAbsent(key, null);
                    }

                    notifyAfterLoad(startTime, System.currentTimeMillis());
                }
                cached = cache.get(foreignKey);
            }
            return cached;
        }
    }

    private void notifyCacheHit(Object foreignKey, Object cached) {
        if (LoadObserverRegistry.isBound()) {
            LoadObserver obs = LoadObserverRegistry.get();
            obs.onCacheHit(new LoadObserver.CacheHitEvent(
                    resultConstructor.getClass(),
                    foreignKey,
                    cached
            ));
        }
    }

    private void notifyBeforeLoad(long startTime) {
        if (LoadObserverRegistry.isBound()) {
            LoadObserver obs = LoadObserverRegistry.get();
            obs.onBeforeLoad(new LoadObserver.BatchLoadEvent(
                    resultConstructor.getClass(),
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
                    resultConstructor.getClass(),
                    foreignKeys,
                    startTime,
                    endTime
            ));
        }
    }
}