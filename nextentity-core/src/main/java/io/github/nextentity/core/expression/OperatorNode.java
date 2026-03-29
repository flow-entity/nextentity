package io.github.nextentity.core.expression;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;

///
/// Expression node representing an operator applied to operands.
/// <p>
/// OperatorNode is a composite expression that applies an {@link Operator}
/// to a list of operand expressions. Supports optimization for certain
/// operators like NOT and multivalued operators (AND, OR).
///
/// @param operands the list of operand expressions
/// @param operator the operator to apply
/// @author HuangChengwei
/// @since 1.0.0
///
public record OperatorNode(
        ImmutableList<ExpressionNode> operands,
        Operator operator
) implements ExpressionNode, SelectItem {

    ///
    /// Applies an operator to this node with additional operands.
    /// <p>
    /// Includes optimizations:
    /// <ul>
    ///   <li>NOT NOT returns the first operand</li>
    ///   <li>Multivalued operators (AND, OR) merge operands when same operator</li>
    /// </ul>
    ///
    /// @param operator the operator to apply
    /// @param nodes additional operand nodes
    /// @return the resulting expression node
    ///
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

    ///
    /// Gets the first operand.
    ///
    /// @return the first operand expression
    ///
    public ExpressionNode firstOperand() {
        return operands.getFirst();
    }

    ///
    /// Gets the second operand if present.
    ///
    /// @return the second operand, or null if only one operand
    ///
    public ExpressionNode secondOperand() {
        return operands.size() > 1 ? operands.get(1) : null;
    }

    ///
    /// Gets the third operand if present.
    ///
    /// @return the third operand, or null if fewer than three operands
    ///
    public ExpressionNode thirdOperand() {
        return operands.size() > 2 ? operands.get(2) : null;
    }

    ///
    /// Returns this operator node as its own expression.
    ///
    /// @return this node
    ///
    @Override
    public ExpressionNode expression() {
        return this;
    }
}
