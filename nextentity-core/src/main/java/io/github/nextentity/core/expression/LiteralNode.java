package io.github.nextentity.core.expression;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;

/**
 * Expression node representing a literal value.
 * <p>
 * LiteralNode wraps a constant value for use in expressions,
 * such as numbers, strings, or booleans.
 * <p>
 * Includes optimization for boolean literals with pre-defined
 * TRUE and FALSE instances.
 *
 * @param value the literal value
 * @author HuangChengwei
 * @since 1.0.0
 */
public record LiteralNode(Object value) implements ExpressionNode, SelectItem {

    /**
     * Pre-defined instance for boolean true.
     */
    public static LiteralNode TRUE = new LiteralNode(true);

    /**
     * Pre-defined instance for boolean false.
     */
    public static LiteralNode FALSE = new LiteralNode(false);

    /**
     * Applies an operator to this literal with additional operands.
     * <p>
     * Includes special optimizations for boolean literals:
     * <ul>
     *   <li>NOT true returns false, NOT false returns true</li>
     *   <li>false AND anything returns false</li>
     *   <li>true OR anything returns true</li>
     * </ul>
     *
     * @param operator the operator to apply
     * @param nodes additional operand nodes
     * @return the resulting expression node
     */
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

    /**
     * Returns this literal node as its own expression.
     *
     * @return this node
     */
    @Override
    public ExpressionNode expression() {
        return this;
    }
}
