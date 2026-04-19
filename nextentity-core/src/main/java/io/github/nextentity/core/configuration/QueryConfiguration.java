package io.github.nextentity.core.configuration;

import io.github.nextentity.core.PaginationConfig;

/// 查询配置接口
///
/// 定义查询相关的配置项，包括分页配置等。
public interface QueryConfiguration {

    /// 分页配置
    ///
    /// 控制分页查询的行为，如自动添加主键排序等。
    ///
    /// @return 分页配置实例
    PaginationConfig paginationConfig();

    /// 创建默认查询配置
    static QueryConfiguration of(PaginationConfig paginationConfig) {
        return () -> paginationConfig;
    }

    /// 默认查询配置（使用默认分页配置）
    QueryConfiguration DEFAULT = of(PaginationConfig.DEFAULT);
}
