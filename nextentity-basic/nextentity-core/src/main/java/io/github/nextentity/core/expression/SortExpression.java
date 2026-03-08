package io.github.nextentity.core.expression;

import io.github.nextentity.api.SortOrder;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.util.ImmutableList;

import java.util.List;

public record SortExpression(ExpressionNode expression, SortOrder order) {

    public static <T> ImmutableList<SortExpression> mapping(List<? extends Order<T>> orders) {
        return orders.stream().map(SortExpression::of).collect(ImmutableList.collector(orders.size()));
    }

    public static SortExpression of(Order<?> order) {
        ExpressionNode node = ExpressionNodes.getNode(order.expression());
        return new SortExpression(node, order.order());
    }

}
