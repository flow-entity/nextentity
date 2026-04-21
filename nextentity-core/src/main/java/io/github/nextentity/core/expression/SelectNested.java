package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;

/// 表示嵌套选择的SELECT子句的记录。
///
/// 用于包含嵌套的选择结构，允许组合多个选择项。
///
/// @param items    嵌套的选择列表
/// @param distinct 是否应用DISTINCT
/// @author HuangChengwei
/// @since 1.0.0
public record SelectNested(ImmutableList<Selected> items, boolean distinct) implements Selected {
}