package io.github.nextentity.core.expression;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;

public record LiteralNode(Object value) implements ExpressionNode, SelectItem {
    public static LiteralNode TRUE = new LiteralNode(true);
    public static LiteralNode FALSE = new LiteralNode(false);

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

    @Override
    public ExpressionNode expression() {
        return this;
    }
}
