package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableArray;

/// 表示选择多个表达式的SELECT子句的记录。
///
/// 用于选择多个列或计算值的查询。
///
/// @param items 要选择的表达式
/// @param distinct 是否应用DISTINCT
/// @author HuangChengwei
/// @since 1.0.0
public record SelectExpressions(ImmutableArray<ExpressionNode> items, boolean distinct) implements Selected {
}
