package io.github.nextentity.core;

import io.github.nextentity.core.meta.Metamodel;

/// 查询操作共享依赖的上下文接口。
///
/// 封装 metamodel、queryExecutor 和 paginationConfig，
/// 提供统一的访问接口，便于构造函数签名简化。
///
/// @author HuangChengwei
/// @since 2.1.2
public interface QueryContext {

    /// 返回实体元数据注册表。
    ///
    /// @return Metamodel 实例
    Metamodel metamodel();

    /// 返回查询执行引擎。
    ///
    /// @return QueryExecutor 实例
    QueryExecutor queryExecutor();

    /// 返回分页配置。
    ///
    /// @return PaginationConfig 实例
    PaginationConfig paginationConfig();
}