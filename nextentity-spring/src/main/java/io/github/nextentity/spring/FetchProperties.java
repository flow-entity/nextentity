package io.github.nextentity.spring;

import jakarta.persistence.FetchType;

/// 懒加载配置属性。
///
/// 配置示例：
/// ```yaml
/// nextentity:
///   fetch:
///     default-type: LAZY
///     batch-max-size: 1000
///     lazy-load-enabled: true
/// ```
///
/// @author HuangChengwei
/// @since 2.1.0
public class FetchProperties {

    /// 默认加载策略
    ///
    /// 未标注 @Fetch 注解时使用的默认策略：
    /// - LAZY: 延迟加载（默认）
    /// - EAGER: 立即加载
    private FetchType defaultType = FetchType.EAGER;

    /// 批量加载的最大批次大小
    ///
    /// 当多个懒加载代理被同时访问时，
    /// 会将 ID 分批查询以避免 SQL 过长。
    /// 默认 1000，建议范围 100-2000。
    private int batchMaxSize = 1000;

    /// 是否启用懒加载功能
    ///
    /// - true: LAZY 属性使用代理延迟加载（默认）
    /// - false: 所有属性都立即加载
    private boolean lazyLoadEnabled = true;

    public FetchType getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(FetchType defaultType) {
        this.defaultType = defaultType;
    }

    public int getBatchMaxSize() {
        return batchMaxSize;
    }

    public void setBatchMaxSize(int batchMaxSize) {
        this.batchMaxSize = batchMaxSize;
    }

    public boolean isLazyLoadEnabled() {
        return lazyLoadEnabled;
    }

    public void setLazyLoadEnabled(boolean lazyLoadEnabled) {
        this.lazyLoadEnabled = lazyLoadEnabled;
    }

    /// 转换为 FetchConfig。
    ///
    /// @return FetchConfig 实例
    public io.github.nextentity.jdbc.FetchConfig toFetchConfig() {
        return new io.github.nextentity.jdbc.FetchConfig(
                defaultType, batchMaxSize, lazyLoadEnabled
        );
    }
}