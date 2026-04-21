package io.github.nextentity.jdbc;

import jakarta.persistence.FetchType;
import org.jspecify.annotations.Nullable;

/// 懒加载配置。
///
/// 控制实体关联属性的加载策略和行为。
///
/// @param defaultFetchType    默认加载策略，未标注 @Fetch 时使用
/// @param batchMaxSize        批量加载的最大批次大小
/// @param lazyLoadEnabled     是否启用懒加载功能
///
/// @author HuangChengwei
/// @since 2.1.0
public record FetchConfig(
        FetchType defaultFetchType,
        int batchMaxSize,
        boolean lazyLoadEnabled
) {

    /// 默认配置实例
    public static final FetchConfig DEFAULT = new FetchConfig(
            FetchType.LAZY, 1000, true
    );

    /// 创建默认配置。
    public FetchConfig() {
        this(FetchType.LAZY, 1000, true);
    }

    /// 紧凑构造函数，验证参数合法性。
    public FetchConfig {
        if (batchMaxSize <= 0) {
            throw new IllegalArgumentException("batchMaxSize must be positive, but was: " + batchMaxSize);
        }
        if (defaultFetchType == null) {
            throw new IllegalArgumentException("defaultFetchType must not be null");
        }
    }

    /// 创建构建器。
    public static Builder builder() {
        return new Builder();
    }

    /// 配置构建器。
    public static class Builder {
        private FetchType defaultFetchType = FetchType.LAZY;
        private int batchMaxSize = 1000;
        private boolean lazyLoadEnabled = true;

        public Builder defaultFetchType(FetchType defaultFetchType) {
            this.defaultFetchType = defaultFetchType;
            return this;
        }

        public Builder batchMaxSize(int batchMaxSize) {
            this.batchMaxSize = batchMaxSize;
            return this;
        }

        public Builder lazyLoadEnabled(boolean lazyLoadEnabled) {
            this.lazyLoadEnabled = lazyLoadEnabled;
            return this;
        }

        public FetchConfig build() {
            return new FetchConfig(defaultFetchType, batchMaxSize, lazyLoadEnabled);
        }
    }
}