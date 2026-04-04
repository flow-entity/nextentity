package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;

/// 表示选择实体列的SELECT子句的记录。
///
/// 用于选择完整实体或实体中的特定字段时。
/// 获取列表指定要急切获取的关联实体。
///
/// @param fetch 要获取的关联路径
/// @param distinct 是否应用DISTINCT
/// @author HuangChengwei
/// @since 1.0.0
public record SelectEntity(ImmutableList<PathNode> fetch, boolean distinct) implements Selected {
}
