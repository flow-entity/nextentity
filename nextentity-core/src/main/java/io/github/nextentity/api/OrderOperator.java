package io.github.nextentity.api;

import io.github.nextentity.api.model.Order;
import io.github.nextentity.api.model.Slice;
import io.github.nextentity.api.model.SliceBuilder;
import io.github.nextentity.api.model.Sliceable;
import jakarta.persistence.LockModeType;

import java.util.Collection;
import java.util.List;

/// Sort operator interface, providing sorting-related operation methods.
///
/// Extends OrderByStep, providing ascending, descending and other sorting operations.
///
/// @param <T> Entity type
/// @param <U> Result type
/// @author HuangChengwei
/// @since 1.0.0
public interface OrderOperator<T, U> extends OrderByStep<T, U> {
    /// Sort in ascending order.
    ///
    /// @return OrderByStep instance
    default OrderByStep<T, U> asc() {
        return sort(SortOrder.ASC);
    }

    /// Sort in descending order.
    ///
    /// @return OrderByStep instance
    default OrderByStep<T, U> desc() {
        return sort(SortOrder.DESC);
    }

    /// Sort by the specified sort order.
    ///
    /// @param order Sort order
    /// @return OrderByStep instance
    OrderByStep<T, U> sort(SortOrder order);

    /// Sort by the specified collection of paths.
    ///
    /// @param paths Collection of paths
    /// @return OrderOperator instance
    @Override
    default OrderOperator<T, U> orderBy(Collection<PathRef<T, ? extends Comparable<?>>> paths) {
        return asc().orderBy(paths);
    }

    @Override
    default long count() {
        return asc().count();
    }

    @Override
    default boolean exists() {
        return asc().exists();
    }

    @Override
    default Collector<U> lock(LockModeType lockModeType) {
        return asc().lock(lockModeType);
    }

    @Override
    default List<U> list() {
        return asc().list();
    }

    @Override
    default List<U> limit(int limit) {
        return asc().limit(limit);
    }

    @Override
    default List<U> window(int offset, int limit) {
        return asc().window(offset, limit);
    }

    @Override
    default U first() {
        return asc().first();
    }

    @Override
    default U single() {
        return asc().single();
    }

    @Override
    default <R> R slice(Sliceable<U, R> sliceable) {
        return asc().slice(sliceable);
    }

    @Override
    default Slice<U> slice(int offset, int limit) {
        return asc().slice(offset, limit);
    }

    @Override
    default <X> SubQueryBuilder<X, U> toSubQuery() {
        return asc().toSubQuery();
    }

    @Override
    default LockStep<U> orderBy(List<? extends Order<T>> orders) {
        return asc().orderBy(orders);
    }
}
