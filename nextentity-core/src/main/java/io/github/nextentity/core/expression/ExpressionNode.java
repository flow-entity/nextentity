package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Sealed interface representing a node in the expression tree.
 * <p>
 * ExpressionNode is the foundation of the expression system, representing
 * all types of expressions in query building: paths, literals, operators,
 * and complete query structures.
 * <p>
 * Permitted subtypes:
 * <ul>
 *   <li>{@link LiteralNode} - literal values</li>
 *   <li>{@link PathNode} - entity attribute paths</li>
 *   <li>{@link OperatorNode} - operator expressions</li>
 *   <li>{@link EmptyNode} - empty/no-op expressions</li>
 *   <li>{@link QueryStructure} - complete query structures</li>
 * </ul>
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public sealed interface ExpressionNode permits LiteralNode, PathNode, OperatorNode, EmptyNode, QueryStructure {

    /**
     * Logger for expression node operations.
     */
    Logger log = LoggerFactory.getLogger(ExpressionNode.class);

    /**
     * Applies a unary operator to this expression.
     *
     * @param operator the operator to apply
     * @return a new expression node with the operator applied
     */
    default ExpressionNode operate(Operator operator) {
        return operate(operator, Collections.emptyList());
    }

    /**
     * Applies a binary operator to this expression and another node.
     *
     * @param operator the operator to apply
     * @param node the right operand
     * @return a new expression node with the operator applied
     */
    default ExpressionNode operate(Operator operator, ExpressionNode node) {
        if(node instanceof EmptyNode) {
            return this;
        }
        return operate(operator, Collections.singleton(node));
    }

    /**
     * Applies a ternary operator to this expression and two other nodes.
     *
     * @param operator the operator to apply
     * @param node0 the first right operand
     * @param node1 the second right operand
     * @return a new expression node with the operator applied
     */
    default ExpressionNode operate(Operator operator, ExpressionNode node0, ExpressionNode node1) {
        return operate(operator, List.of(node0, node1));
    }

    /**
     * Applies an operator to this expression and an array of nodes.
     *
     * @param operator the operator to apply
     * @param nodes the operand array
     * @return a new expression node with the operator applied
     */
    default ExpressionNode operate(Operator operator, ExpressionNode[] nodes) {
        return nodes.length == 0 ? operate(operator, Collections.emptyList()) : operate(operator, Arrays.asList(nodes));
    }

    /**
     * Applies an operator to this expression and a collection of nodes.
     * <p>
     * Creates a new OperatorNode containing this expression and all operands.
     * Special handling for IN operator with empty operands returns FALSE.
     *
     * @param operator the operator to apply
     * @param nodes the operand collection
     * @return a new expression node with the operator applied
     */
    default ExpressionNode operate(Operator operator, Collection<ExpressionNode> nodes) {
        if (operator == Operator.IN && nodes.isEmpty()) {
            log.warn("Operator 'IN' with empty operands detected, returning FALSE expression");
            return LiteralNode.FALSE;
        }
        ImmutableList.Builder<ExpressionNode> builder;
        builder = new ImmutableList.Builder<>(nodes.size() + 1);
        builder.add(this);
        builder.addAll(nodes);
        return new OperatorNode(builder.build(), operator);
    }

}
