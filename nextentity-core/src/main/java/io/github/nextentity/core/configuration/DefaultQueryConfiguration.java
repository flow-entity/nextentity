package io.github.nextentity.core.configuration;

import io.github.nextentity.core.PaginationConfig;

/// 默认查询配置实现类
///
/// 提供查询配置的具体实现，包含分页配置。
/// 使用 Builder 模式构造，保证不可变性。
public class DefaultQueryConfiguration implements QueryConfiguration {

    private final PaginationConfig paginationConfig;

    private DefaultQueryConfiguration(PaginationConfig paginationConfig) {
        this.paginationConfig = paginationConfig;
    }

    @Override
    public PaginationConfig paginationConfig() {
        return paginationConfig;
    }

    /// 默认配置实例
    public static final DefaultQueryConfiguration DEFAULT =
            new DefaultQueryConfiguration(PaginationConfig.DEFAULT);

    /// 创建构建器
    public static Builder builder() {
        return new Builder();
    }

    /// 配置构建器
    public static class Builder {
        private PaginationConfig paginationConfig = PaginationConfig.DEFAULT;

        public Builder paginationConfig(PaginationConfig paginationConfig) {
            this.paginationConfig = paginationConfig;
            return this;
        }

        public DefaultQueryConfiguration build() {
            return new DefaultQueryConfiguration(paginationConfig);
        }
    }
}