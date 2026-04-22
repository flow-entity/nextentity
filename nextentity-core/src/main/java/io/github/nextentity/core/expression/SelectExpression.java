package io.github.nextentity.core.expression;

/// 表示选择单个表达式的SELECT子句的记录。
///
/// 用于选择单个列或计算值的查询。
///
/// @param expression 要选择的表达式
/// @param distinct   是否应用DISTINCT
/// @author HuangChengwei
/// @since 1.0.0
public record SelectExpression(ExpressionNode expression, boolean distinct) implements Selected {
}
