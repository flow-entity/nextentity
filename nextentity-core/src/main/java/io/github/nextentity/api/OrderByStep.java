package io.github.nextentity.api;

import io.github.nextentity.api.model.Order;

import java.util.Collection;
import java.util.List;

/// Sorting step interface for query result sorting construction.
///
/// Extends Collector to specify the sorting method of query results.
///
/// @param <T> Entity type
/// @param <U> Result type
/// @author HuangChengwei
/// @since 1.0.0
public interface OrderByStep<T, U> extends Collector<U> {

    /// Sort by the specified list of sorting rules.
    ///
    /// @param orders List of sorting rules
    /// @return Collector instance
    Collector<U> orderBy(List<? extends Order<T>> orders);

    /// Sort by a single sorting rule.
    ///
    /// @param order Sorting rule
    /// @return Collector instance
    default Collector<U> orderBy(Order<T> order) {
        return orderBy(List.of(order));
    }

    /// Sort by two sorting rules.
    ///
    /// @param p0 First sorting rule
    /// @param p1 Second sorting rule
    /// @return Collector instance
    default Collector<U> orderBy(Order<T> p0, Order<T> p1) {
        return orderBy(List.of(p0, p1));
    }

    /// Sort by three sorting rules.
    ///
    /// @param order1 First sorting rule
    /// @param order2 Second sorting rule
    /// @param order3 Third sorting rule
    /// @return Collector instance
    default Collector<U> orderBy(Order<T> order1, Order<T> order2, Order<T> order3) {
        return orderBy(List.of(order1, order2, order3));
    }

    /// Sort by the specified collection of paths.
    ///
    /// @param paths Collection of paths
    /// @return OrderOperator instance
    OrderOperator<T, U> orderBy(Collection<PathRef<T, ? extends Comparable<?>>> paths);

    /// Sort by a single path.
    ///
    /// @param path Path
    /// @return OrderOperator instance
    default OrderOperator<T, U> orderBy(PathRef<T, ? extends Comparable<?>> path) {
        return orderBy(List.of(path));
    }

    /// Sort by two paths.
    ///
    /// @param p1 First path
    /// @param p2 Second path
    /// @return OrderOperator instance
    default OrderOperator<T, U> orderBy(PathRef<T, ? extends Comparable<?>> p1, PathRef<T, ? extends Comparable<?>> p2) {
        return orderBy(List.of(p1, p2));
    }

    /// Sort by three paths.
    ///
    /// @param p1 First path
    /// @param p2 Second path
    /// @param p3 Third path
    /// @return OrderOperator instance
    default OrderOperator<T, U> orderBy(PathRef<T, ? extends Comparable<?>> p1, PathRef<T, ? extends Comparable<?>> p2, PathRef<T, ? extends Comparable<?>> p3) {
        return orderBy(List.of(p1, p2, p3));
    }

}
