package io.github.nextentity.core;

import io.github.nextentity.core.meta.Metamodel;

/// QueryContext 的简单 record 实现。
///
/// @param metamodel       实体元数据注册表
/// @param queryExecutor   查询执行引擎
/// @param paginationConfig 分页配置
/// @author HuangChengwei
/// @since 2.1.2
public record SimpleQueryContext(
        Metamodel metamodel,
        QueryExecutor queryExecutor,
        PaginationConfig paginationConfig
) implements QueryContext {
}