package io.github.nextentity.core;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.LiteralNode;
import io.github.nextentity.core.expression.OperatorNode;
import io.github.nextentity.core.meta.EntityAttribute;

/// 表示 SELECT 子句中可选择项的密封接口。
///
/// 允许的子类型：
/// - {@link LiteralNode}：字面量值
/// - {@link OperatorNode}：运算符表达式
/// - {@link EntityAttribute}：实体属性
///
/// @author HuangChengwei
/// @since 1.0.0
public sealed interface SelectItem permits LiteralNode, OperatorNode, EntityAttribute {

    ExpressionNode expression();

}
