package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public sealed interface ExpressionNode permits LiteralNode, PathNode, OperatorNode, EmptyNode, QueryStructure {
    Logger log = LoggerFactory.getLogger(ExpressionNode.class);

    default ExpressionNode operate(Operator operator) {
        return operate(operator, Collections.emptyList());
    }

    default ExpressionNode operate(Operator operator, ExpressionNode node) {
        if(node instanceof EmptyNode) {
            return this;
        }
        return operate(operator, Collections.singleton(node));
    }

    default ExpressionNode operate(Operator operator, ExpressionNode node0, ExpressionNode node1) {
        return operate(operator, List.of(node0, node1));
    }

    default ExpressionNode operate(Operator operator, ExpressionNode[] nodes) {
        return nodes.length == 0 ? operate(operator, Collections.emptyList()) : operate(operator, Arrays.asList(nodes));
    }

    default ExpressionNode operate(Operator operator, Collection<ExpressionNode> nodes) {
        if (operator == Operator.IN && nodes.isEmpty()) {
            log.warn("operator `in` right operands is empty");
            return LiteralNode.FALSE;
        }
        ImmutableList.Builder<ExpressionNode> builder;
        builder = new ImmutableList.Builder<>(nodes.size() + 1);
        builder.add(this);
        builder.addAll(nodes);
        return new OperatorNode(builder.build(), operator);
    }

}
