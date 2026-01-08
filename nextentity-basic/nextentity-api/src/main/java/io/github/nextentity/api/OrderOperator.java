package io.github.nextentity.api;

import io.github.nextentity.api.model.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * 排序操作符接口，提供排序相关的操作方法。
 * <p>
 * 继承自SelectOrderByStep，提供升序、降序等排序操作。
 *
 * @param <T> 实体类型
 * @param <U> 结果类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public
interface OrderOperator<T, U> extends SelectOrderByStep<T, U> {
    /**
     * 按升序排序。
     *
     * @return SelectOrderByStep实例
     */
    default SelectOrderByStep<T, U> asc() {
        return sort(SortOrder.ASC);
    }

    /**
     * 按降序排序。
     *
     * @return SelectOrderByStep实例
     */
    default SelectOrderByStep<T, U> desc() {
        return sort(SortOrder.DESC);
    }

    /**
     * 按指定排序方式排序。
     *
     * @param order 排序方式
     * @return SelectOrderByStep实例
     */
    SelectOrderByStep<T, U> sort(SortOrder order);

    /**
     * 按指定路径集合排序。
     *
     * @param paths 路径集合
     * @return OrderOperator实例
     */
    @Override
    default OrderOperator<T, U> orderBy(Collection<Path<T, Comparable<?>>> paths) {
        return asc().orderBy(paths);
    }

    /**
     * 统计结果数量。
     *
     * @return 结果数量
     */
    @Override
    default long count() {
        return asc().count();
    }

    /**
     * 获取指定偏移量和最大结果数的结果列表，并应用指定的锁定模式。
     *
     * @param offset 偏移量
     * @param maxResult 最大结果数
     * @param lockModeType 锁定模式
     * @return 结果列表
     */
    @Override
    default List<U> getList(int offset, int maxResult, LockModeType lockModeType) {
        return asc().getList(offset, maxResult, lockModeType);
    }

    /**
     * 检查是否存在指定偏移量的结果。
     *
     * @param offset 偏移量
     * @return 是否存在
     */
    @Override
    default boolean exist(int offset) {
        return asc().exist(offset);
    }

    /**
     * 对结果进行切片操作。
     *
     * @param sliceable 切片操作器
     * @param <R> 切片结果类型
     * @return 切片结果
     */
    @Override
    default <R> R slice(Sliceable<U, R> sliceable) {
        return asc().slice(sliceable);
    }

    /**
     * 对结果进行切片操作，返回指定偏移量和限制的结果。
     *
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Slice实例
     */
    @Override
    default Slice<U> slice(int offset, int limit) {
        return asc().slice(offset, limit);
    }

    /**
     * 将查询转换为子查询构建器。
     *
     * @param <X> 子查询实体类型
     * @return 子查询构建器
     */
    @Override
    default <X> SubQueryBuilder<X, U> asSubQuery() {
        return asc().asSubQuery();
    }

    /**
     * 获取指定分页参数的分页结果。
     *
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Override
    default Page<U> getPage(Pageable pageable) {
        return asc().getPage(pageable);
    }

    /**
     * 对结果进行映射操作。
     *
     * @param mapper 映射函数
     * @param <R> 映射结果类型
     * @return Collector实例
     */
    @Override
    default <R> Collector<R> map(Function<? super U, ? extends R> mapper) {
        return asc().map(mapper);
    }

    /**
     * 按指定排序条件排序。
     *
     * @param orders 排序条件列表
     * @return Collector实例
     */
    @Override
    default Collector<U> orderBy(List<? extends Order<T>> orders) {
        return asc().orderBy(orders);
    }

    /**
     * 按指定排序条件构建器构建的排序条件排序。
     *
     * @param ordersBuilder 排序条件构建器
     * @return Collector实例
     */
    @Override
    default Collector<U> orderBy(Function<EntityRoot<T>, List<? extends Order<T>>> ordersBuilder) {
        return asc().orderBy(ordersBuilder);
    }

}
