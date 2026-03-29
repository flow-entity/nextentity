package io.github.nextentity.core.expression;

import io.github.nextentity.api.SortOrder;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.util.ImmutableList;

import java.util.List;

///
/// Record representing a sort expression in ORDER BY clause.
/// <p>
/// Combines an expression to sort by with a sort order (ASC/DESC).
///
/// @param expression the expression to sort by
/// @param order the sort order (ascending or descending)
/// @author HuangChengwei
/// @since 1.0.0
///
public record SortExpression(ExpressionNode expression, SortOrder order) {

    ///
    /// Maps a list of Order objects to SortExpression instances.
    ///
    /// @param orders the Order objects to map
    /// @param <T> the entity type
    /// @return an immutable list of SortExpression instances
    ///
    public static <T> ImmutableList<SortExpression> mapping(List<? extends Order<T>> orders) {
        return orders.stream().map(SortExpression::of).collect(ImmutableList.collector(orders.size()));
    }

    ///
    /// Creates a SortExpression from an Order object.
    ///
    /// @param order the Order object
    /// @return a new SortExpression instance
    ///
    public static SortExpression of(Order<?> order) {
        ExpressionNode node = ExpressionNodes.getNode(order.expression());
        return new SortExpression(node, order.order());
    }

}
