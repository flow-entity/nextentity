package io.github.nextentity.api;

import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.api.model.Order;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * 选择排序步骤接口，提供查询结果的排序构建方法。
 * <p>
 * 继承自Collector和EntityRootProvider，用于指定查询结果的排序方式。
 *
 * @param <T> 实体类型
 * @param <U> 结果类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public
interface SelectOrderByStep<T, U> extends Collector<U>, EntityRootProvider<T> {

    /**
     * 按照指定的排序规则列表进行排序。
     *
     * @param orders 排序规则列表
     * @return Collector实例
     */
    Collector<U> orderBy(List<? extends Order<T>> orders);

    /**
     * 通过函数构建排序规则并进行排序。
     *
     * @param ordersBuilder 排序规则构建函数
     * @return Collector实例
     */
    Collector<U> orderBy(Function<EntityRoot<T>, List<? extends Order<T>>> ordersBuilder);

    /**
     * 按照单个排序规则进行排序。
     *
     * @param order 排序规则
     * @return Collector实例
     */
    default Collector<U> orderBy(Order<T> order) {
        return orderBy(List.of(order));
    }

    /**
     * 按照两个排序规则进行排序。
     *
     * @param p0 第一个排序规则
     * @param p1 第二个排序规则
     * @return Collector实例
     */
    default Collector<U> orderBy(Order<T> p0, Order<T> p1) {
        return orderBy(List.of(p0, p1));
    }

    /**
     * 按照三个排序规则进行排序。
     *
     * @param order1 第一个排序规则
     * @param order2 第二个排序规则
     * @param order3 第三个排序规则
     * @return Collector实例
     */
    default Collector<U> orderBy(Order<T> order1, Order<T> order2, Order<T> order3) {
        return orderBy(List.of(order1, order2, order3));
    }

    /**
     * 按照指定的路径集合进行排序。
     *
     * @param paths 路径集合
     * @return OrderOperator实例
     */
    OrderOperator<T, U> orderBy(Collection<Path<T, ? extends Comparable<?>>> paths);

    /**
     * 按照单个路径进行排序。
     *
     * @param path 路径
     * @return OrderOperator实例
     */
    default OrderOperator<T, U> orderBy(Path<T, ? extends Comparable<?>> path) {
        return orderBy(List.of(path));
    }

    /**
     * 按照两个路径进行排序。
     *
     * @param p1 第一个路径
     * @param p2 第二个路径
     * @return OrderOperator实例
     */
    default OrderOperator<T, U> orderBy(Path<T, ? extends Comparable<?>> p1, Path<T, ? extends Comparable<?>> p2) {
        return orderBy(List.of(p1, p2));
    }

    /**
     * 按照三个路径进行排序。
     *
     * @param p1 第一个路径
     * @param p2 第二个路径
     * @param p3 第三个路径
     * @return OrderOperator实例
     */
    default OrderOperator<T, U> orderBy(Path<T, ? extends Comparable<?>> p1, Path<T, ? extends Comparable<?>> p2, Path<T, ? extends Comparable<?>> p3) {
        return orderBy(List.of(p1, p2, p3));
    }

}
