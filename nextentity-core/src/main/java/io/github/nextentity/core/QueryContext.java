package io.github.nextentity.core;

import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

/// 查询操作共享依赖的上下文接口。
///
/// 封装 metamodel、queryExecutor、paginationConfig、entityType 和 entityClass，
/// 提供统一的访问接口，便于构造函数签名简化。
///
/// @param <E> 实体类型
/// @author HuangChengwei
/// @since 2.1.2
public interface QueryContext<E> {

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

    /// 返回实体类型元数据。
    ///
    /// @return EntityType 实例
    EntityType entityType();

    /// 返回实体类。
    ///
    /// @return 实体 Class 实例
    Class<E> entityClass();
}