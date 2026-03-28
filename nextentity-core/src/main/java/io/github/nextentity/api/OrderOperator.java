package io.github.nextentity.api;

import io.github.nextentity.api.model.*;
import jakarta.persistence.LockModeType;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Sort operator interface, providing sorting-related operation methods.
 * <p>
 * Extends OrderByStep, providing ascending, descending and other sorting operations.
 *
 * @param <T> Entity type
 * @param <U> Result type
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface OrderOperator<T, U> extends OrderByStep<T, U> {
    /**
     * Sort in ascending order.
     *
     * @return OrderByStep instance
     */
    default OrderByStep<T, U> asc() {
        return sort(SortOrder.ASC);
    }

    /**
     * Sort in descending order.
     *
     * @return OrderByStep instance
     */
    default OrderByStep<T, U> desc() {
        return sort(SortOrder.DESC);
    }

    /**
     * Sort by the specified sort order.
     *
     * @param order Sort order
     * @return OrderByStep instance
     */
    OrderByStep<T, U> sort(SortOrder order);

    /**
     * Sort by the specified collection of paths.
     *
     * @param paths Collection of paths
     * @return OrderOperator instance
     */
    @Override
    default OrderOperator<T, U> orderBy(Collection<Path<T, ? extends Comparable<?>>> paths) {
        return asc().orderBy(paths);
    }

    /**
     * Count the number of results.
     *
     * @return Number of results
     */
    @Override
    default long count() {
        return asc().count();
    }

    /**
     * Get the list of results with the specified offset and maximum number of results, and apply the specified lock mode.
     *
     * @param offset Offset
     * @param maxResult Maximum number of results
     * @param lockModeType Lock mode
     * @return List of results
     */
    @Override
    default List<U> getList(int offset, int maxResult, LockModeType lockModeType) {
        return asc().getList(offset, maxResult, lockModeType);
    }

    /**
     * Check if there are results at the specified offset.
     *
     * @param offset Offset
     * @return Whether exists
     */
    @Override
    default boolean exist(int offset) {
        return asc().exist(offset);
    }

    /**
     * Perform slice operation on the results.
     *
     * @param sliceable Slice operator
     * @param <R> Slice result type
     * @return Slice result
     */
    @Override
    default <R> R slice(Sliceable<U, R> sliceable) {
        return asc().slice(sliceable);
    }

    /**
     * Perform slice operation on the results, returning results with the specified offset and limit.
     *
     * @param offset Offset
     * @param limit Limit number
     * @return Slice instance
     */
    @Override
    default Slice<U> slice(int offset, int limit) {
        return asc().slice(offset, limit);
    }

    /**
     * Convert the query to a subquery builder.
     *
     * @param <X> Subquery entity type
     * @return Subquery builder
     */
    @Override
    default <X> SubQueryBuilder<X, U> asSubQuery() {
        return asc().asSubQuery();
    }

    /**
     * Get paged results with the specified paging parameters.
     *
     * @param pageable Paging parameters
     * @return Paged results
     */
    @Override
    default Page<U> getPage(Pageable pageable) {
        return asc().getPage(pageable);
    }

    /**
     * Perform mapping operation on the results.
     *
     * @param mapper Mapping function
     * @param <R> Mapping result type
     * @return Collector instance
     */
    @Override
    default <R> Collector<R> map(Function<? super U, ? extends R> mapper) {
        return asc().map(mapper);
    }

    /**
     * Sort by the specified sorting conditions.
     *
     * @param orders List of sorting conditions
     * @return Collector instance
     */
    @Override
    default Collector<U> orderBy(List<? extends Order<T>> orders) {
        return asc().orderBy(orders);
    }

}
