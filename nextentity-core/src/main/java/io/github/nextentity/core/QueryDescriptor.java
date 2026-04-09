package io.github.nextentity.core;

import io.github.nextentity.api.EntityDescriptor;

/// 查询操作共享依赖的上下文接口。
///
/// 封装 metamodel、queryExecutor、paginationConfig、entityType 和 entityClass，
/// 提供统一的访问接口，便于构造函数签名简化。
///
/// @param <E> 实体类型
/// @author HuangChengwei
/// @since 2.1.2
public interface QueryDescriptor<E> extends EntityDescriptor<E> {

    /// 返回查询执行引擎。
    ///
    /// @return QueryExecutor 实例
    QueryExecutor queryExecutor();


    /// 返回分页配置。
    ///
    /// @return PaginationConfig 实例
    PaginationConfig paginationConfig();
}