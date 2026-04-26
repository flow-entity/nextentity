package io.github.nextentity.core;

import jakarta.persistence.FetchType;

/// 查询配置属性。
///
/// 集中管理查询行为的所有可配置参数：
/// 加载策略、批量大小、懒加载、分页行为。
public record QueryProperties(
        FetchType defaultFetchType,
        int fetchBatchMaxSize,
        boolean lazyLoadEnabled,
        boolean autoAddIdOrder,
        boolean interfaceLazyEnabled,
        boolean classLazyEnabled
) {

    public static final QueryProperties DEFAULT = new QueryProperties(
            FetchType.LAZY,
            1000,
            true,
            true,
            true,
            false
    );

    public QueryProperties {
        if (fetchBatchMaxSize <= 0) {
            throw new IllegalArgumentException("fetchBatchMaxSize must be positive, but was: " + fetchBatchMaxSize);
        }
        if (defaultFetchType == null) {
            throw new IllegalArgumentException("defaultFetchType must not be null");
        }
    }
}
