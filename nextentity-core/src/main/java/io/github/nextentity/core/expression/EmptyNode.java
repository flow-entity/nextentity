package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;

///
/// Expression node representing an empty/no-op expression.
/// <p>
/// Used as a placeholder for null or missing expressions in the query structure.
/// When combined with other expressions, EmptyNode is typically ignored or
/// replaced by the combined expression.
/// <p>
/// This is a singleton with a single instance available via {@link #INSTANCE}.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public final class EmptyNode implements ExpressionNode {

    ///
    /// The singleton instance of EmptyNode.
    ///
    public static final EmptyNode INSTANCE = new EmptyNode();

    private EmptyNode() {
    }

    ///
    /// Applies an operator to this empty node with additional operands.
    /// <p>
    /// If no additional operands are provided, returns this empty node.
    /// Otherwise, creates a new OperatorNode with the provided operands.
    ///
    /// @param operator the operator to apply
    /// @param nodes additional operand nodes
    /// @return this empty node if no operands, or a new OperatorNode
    ///
    @Override
    public ExpressionNode operate(Operator operator, Collection<ExpressionNode> nodes) {
        if (nodes.isEmpty()) {
            return this;
        }
        return new OperatorNode(ImmutableList.ofCollection(nodes), operator);
    }
}
