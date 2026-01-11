package io.github.nextentity.api;

import io.github.nextentity.api.model.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author HuangChengwei
 * @since 2024-05-06 8:38
 */
public
interface OrderOperator<T, U> extends SelectOrderByStep<T, U> {
    default SelectOrderByStep<T, U> asc() {
        return sort(SortOrder.ASC);
    }

    default SelectOrderByStep<T, U> desc() {
        return sort(SortOrder.DESC);
    }

    SelectOrderByStep<T, U> sort(SortOrder order);

    @Override
    default OrderOperator<T, U> orderBy(Collection<Path<T, ? extends Comparable<?>>> paths) {
        return asc().orderBy(paths);
    }

    @Override
    default long count() {
        return asc().count();
    }

    @Override
    default List<U> getList(int offset, int maxResult, LockModeType lockModeType) {
        return asc().getList(offset, maxResult, lockModeType);
    }

    @Override
    default boolean exist(int offset) {
        return asc().exist(offset);
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
    default <X> SubQueryBuilder<X, U> asSubQuery() {
        return asc().asSubQuery();
    }

    @Override
    default Page<U> getPage(Pageable pageable) {
        return asc().getPage(pageable);
    }

    @Override
    default <R> Collector<R> map(Function<? super U, ? extends R> mapper) {
        return asc().map(mapper);
    }

    @Override
    default Collector<U> orderBy(List<? extends Order<T>> orders) {
        return asc().orderBy(orders);
    }
    @Override
    default Collector<U> orderBy(Function<EntityRoot<T>, List<? extends Order<T>>> ordersBuilder) {
        return asc().orderBy(ordersBuilder);
    }

}
