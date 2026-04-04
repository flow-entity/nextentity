package io.github.nextentity.core.expression;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;

/// 表示文字值的表达式节点。
///
/// LiteralNode 包装一个常量值用于表达式中，
/// 如数字、字符串或布尔值。
///
/// 包含对布尔文字的预定义优化
/// TRUE 和 FALSE 实例。
///
/// @param value 文字值
/// @author HuangChengwei
/// @since 1.0.0
public record LiteralNode(Object value) implements ExpressionNode, SelectItem {

    /// 布尔值 true 的预定义实例。
    public static LiteralNode TRUE = new LiteralNode(true);

    /// 布尔值 false 的预定义实例。
    public static LiteralNode FALSE = new LiteralNode(false);

    /// 将运算符应用于此字面量和额外操作数。
    ///
    /// 包含布尔字面量的特殊优化：
    /// - NOT true 返回 false，NOT false 返回 true
    /// - false AND 任何值返回 false
    /// - true OR 任何值返回 true
    ///
    /// @param operator 要应用的运算符
    /// @param nodes 额外操作数节点
    /// @return 结果表达式节点
    @Override
    public ExpressionNode operate(Operator operator, Collection<ExpressionNode> nodes) {
        if (operator == Operator.NOT) {
            if (value instanceof Boolean b) {
                return b ? FALSE : TRUE;
            }
        }
        if (operator == Operator.AND) {
            if (value instanceof Boolean b) {
                if (b) {
                    return new OperatorNode(ImmutableList.ofCollection(nodes), Operator.AND);
                } else {
                    return FALSE;
                }
            }
        } else if (operator == Operator.OR) {
            if (value instanceof Boolean b) {
                if (b) {
                    return TRUE;
                } else {
                    return new OperatorNode(ImmutableList.ofCollection(nodes), Operator.AND);
                }
            }
        }
        return ExpressionNode.super.operate(operator, nodes);
    }

    /// 返回此字面量节点作为其自身的表达式。
    ///
    /// @return 此节点
    @Override
    public ExpressionNode expression() {
        return this;
    }
}
