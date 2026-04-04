package io.github.nextentity.core.expression;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;

/// 表示应用于操作数的运算符的表达式节点。
///
/// OperatorNode 是一个复合表达式，它将 {@link Operator}
/// 应用于操作数表达式列表。支持某些
/// 运算符的优化，如 NOT 和多值运算符（AND，OR）。
///
/// @param operands 操作数表达式列表
/// @param operator 要应用的运算符
/// @author HuangChengwei
/// @since 1.0.0
public record OperatorNode(
        ImmutableList<ExpressionNode> operands,
        Operator operator
) implements ExpressionNode, SelectItem {

    /// 将运算符应用于此节点和额外操作数。
    ///
    /// 包含优化：
    /// - NOT NOT 返回第一个操作数
    /// - 多值运算符（AND, OR）在相同运算符时合并操作数
    ///
    /// @param operator 要应用的运算符
    /// @param nodes 额外操作数节点
    /// @return 结果表达式节点
    @Override
    public ExpressionNode operate(Operator operator, Collection<ExpressionNode> nodes) {
        if (operator == Operator.NOT && this.operator == Operator.NOT) {
            return operands.getFirst();
        }

        if (operator.isMultivalued() && this.operator == operator) {
            ImmutableList.Builder<ExpressionNode> builder = new ImmutableList.Builder<>(operands.size() + nodes.size());
            builder.addAll(operands.asList());
            builder.addAll(nodes);
            return new OperatorNode(builder.build(), this.operator);
        }
        return ExpressionNode.super.operate(operator, nodes);
    }

    /// 获取第一个操作数。
    ///
    /// @return 第一个操作数表达式
    public ExpressionNode firstOperand() {
        return operands.getFirst();
    }

    /// 获取第二个操作数（如果存在）。
    ///
    /// @return 第二个操作数，如果只有一个操作数则返回 null
    public ExpressionNode secondOperand() {
        return operands.size() > 1 ? operands.get(1) : null;
    }

    /// 获取第三个操作数（如果存在）。
    ///
    /// @return 第三个操作数，如果少于三个操作数则返回 null
    public ExpressionNode thirdOperand() {
        return operands.size() > 2 ? operands.get(2) : null;
    }

    /// 返回此运算符节点作为其自身的表达式。
    ///
    /// @return 此节点
    @Override
    public ExpressionNode expression() {
        return this;
    }
}
