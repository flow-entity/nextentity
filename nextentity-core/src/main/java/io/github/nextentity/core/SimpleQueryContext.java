package io.github.nextentity.core;

import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

/// QueryContext 的简单 record 实现。
///
/// @param <E>             实体类型
/// @param metamodel       实体元数据注册表
/// @param queryExecutor   查询执行引擎
/// @param paginationConfig 分页配置
/// @param entityType      实体类型元数据
/// @param entityClass     实体类
/// @author HuangChengwei
/// @since 2.1.2
public record SimpleQueryContext<E>(
        Metamodel metamodel,
        QueryExecutor queryExecutor,
        PaginationConfig paginationConfig,
        EntityType entityType,
        Class<E> entityClass
) implements QueryContext<E> {
}