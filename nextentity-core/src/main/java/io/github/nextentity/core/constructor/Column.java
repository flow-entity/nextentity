package io.github.nextentity.core.constructor;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.ValueConverter;

/// SQL SELECT 列的抽象
///
/// Column 统一使用 ExpressionNode 作为列来源，支持：
/// - PathNode: 实体属性路径 → alias.columnName
/// - OperatorNode: 函数表达式 → COUNT(*), SUM(salary)
/// - LiteralNode: 常量 → 1, 'hello'
///
/// @param source     列来源（统一使用 ExpressionNode 接口）
/// @param converter  值转换器（从 ResultSet 获取值时使用）
/// @param tableIndex 所属表索引（仅对 PathNode 有效）
/// 0 = 主表，>0 = join 表索引
/// 对于 OperatorNode/LiteralNode，tableIndex = -1（无意义）
/// @author HuangChengwei
/// @since 2.2.2
public record Column(ExpressionNode source, ValueConverter<?, ?> converter, int tableIndex) {

}