package io.github.nextentity.core;

import io.github.nextentity.api.EntityDescriptor;
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
public interface QueryDescriptor<E> {

    EntityDescriptor<E> entityDescriptor();

    QueryConfig queryConfig();

    default Class<E> entityClass() {
        return entityDescriptor().entityClass();
    }

    default Metamodel metamodel() {
        return queryConfig().metamodel();
    }

    default EntityType entityType() {
        return entityDescriptor().entityType();
    }

}