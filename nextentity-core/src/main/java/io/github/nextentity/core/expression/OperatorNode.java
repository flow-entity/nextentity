package io.github.nextentity.core.expression;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;

public record OperatorNode(
        ImmutableList<ExpressionNode> operands,
        Operator operator
) implements ExpressionNode, SelectItem {

    public OperatorNode(ImmutableList<ExpressionNode> operands, Operator operator) {
        this.operands = operands;
        this.operator = operator;
    }

    @Override
    public ExpressionNode operate(Operator operator, Collection<ExpressionNode> nodes) {
        if (operator == Operator.NOT && this.operator == Operator.NOT) {
            return operands.get(0);
        }

        if (operator.isMultivalued() && this.operator == operator) {
            ImmutableList.Builder<ExpressionNode> builder = new ImmutableList.Builder<>(operands.size() + nodes.size());
            builder.addAll(operands.asList());
            builder.addAll(nodes);
            return new OperatorNode(builder.build(), this.operator);
        }
        return ExpressionNode.super.operate(operator, nodes);
    }

    public ExpressionNode firstOperand() {
        return operands.get(0);
    }

    public ExpressionNode secondOperand() {
        return operands.size() > 1 ? operands.get(1) : null;
    }

    public ExpressionNode thirdOperand() {
        return operands.size() > 2 ? operands.get(2) : null;
    }

    @Override
    public ExpressionNode expression() {
        return this;
    }
}
