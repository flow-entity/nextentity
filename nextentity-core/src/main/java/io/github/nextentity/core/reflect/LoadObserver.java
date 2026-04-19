package io.github.nextentity.core.reflect;

import java.util.Set;

/// 延时加载观察器接口。
///
/// 实现此接口可观测延时加载行为，用于测试场景。
/// 通过 {@link LoadObserverRegistry} 注册观察器。
///
/// @author HuangChengwei
/// @since 2.1.0
public interface LoadObserver {

    /// 首次访问 LAZY 属性时调用（加载前）。
    void onBeforeLoad(BatchLoadEvent event);

    /// 批量加载完成后调用（加载后）。
    void onAfterLoad(BatchLoadEvent event);

    /// 从缓存命中时调用（非首次访问）。
    void onCacheHit(CacheHitEvent event);

    /// 批量加载事件。
    record BatchLoadEvent(
            Class<?> entityType,
            Set<?> foreignKeys,
            long startTimeNanos,
            long endTimeNanos
    ) {
        /// 加载耗时（纳秒）。
        public long durationNanos() {
            return endTimeNanos - startTimeNanos;
        }
    }

    /// 缓存命中事件。
    record CacheHitEvent(
            Class<?> entityType,
            Object foreignKey,
            Object cachedValue
    ) {
    }
}