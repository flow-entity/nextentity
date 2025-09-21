package io.github.nextentity.core.expression;

import io.github.nextentity.core.expression.Operator;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;

public final class EmptyNode implements ExpressionNode {
    public static final EmptyNode INSTANCE = new EmptyNode();

    private EmptyNode() {
    }


    @Override
    public ExpressionNode operate(Operator operator, Collection<ExpressionNode> nodes) {
        if (nodes.isEmpty()) {
            return this;
        }
        return new OperatorNode(ImmutableList.ofCollection(nodes), operator);
    }
}
