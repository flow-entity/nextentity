package io.github.nextentity.api;

import io.github.nextentity.api.model.Order;

import java.util.Collection;
import java.util.List;

/// 排序步骤接口，用于查询结果排序构建。
///
/// 继承 LockStep，用于指定查询结果的排序方式。
///
/// ## 使用示例
///
/// ```java
/// // 单字段排序
/// List<User> users = repository.query()
///     .orderBy(User::getName).asc()
///     .list();
///
/// // 多字段排序
/// List<User> users = repository.query()
///     .orderBy(User::getDepartment).asc()
///     .orderBy(User::getName).desc()
///     .list();
///
/// // 使用 Order 对象（用于复用或动态构建）
/// Order<User> order = Path.of(User::getName).asc();
/// List<User> users = repository.query()
///     .orderBy(order)
///     .list();
/// ```
///
/// @param <T> 实体类型
/// @param <U> 结果类型
/// @author HuangChengwei
/// @since 1.0.0
public interface OrderByStep<T, U> extends LockStep<U> {

    /// 按指定的排序规则列表排序。
    ///
    /// @param orders 排序规则列表
    /// @return Collector 实例
    LockStep<U> orderBy(List<? extends Order<T>> orders);

    /// 按单个排序规则排序。
    ///
    /// @param order 排序规则
    /// @return Collector 实例
    default LockStep<U> orderBy(Order<T> order) {
        return orderBy(List.of(order));
    }

    /// 按两个排序规则排序。
    ///
    /// @param p0 第一个排序规则
    /// @param p1 第二个排序规则
    /// @return Collector 实例
    default LockStep<U> orderBy(Order<T> p0, Order<T> p1) {
        return orderBy(List.of(p0, p1));
    }

    /// 按三个排序规则排序。
    ///
    /// @param order1 第一个排序规则
    /// @param order2 第二个排序规则
    /// @param order3 第三个排序规则
    /// @return Collector 实例
    default LockStep<U> orderBy(Order<T> order1, Order<T> order2, Order<T> order3) {
        return orderBy(List.of(order1, order2, order3));
    }

    /// 按指定的路径集合排序。
    ///
    /// @param paths 路径集合
    /// @return OrderOperator 实例
    OrderOperator<T, U> orderBy(Collection<PathRef<T, ? extends Comparable<?>>> paths);

    /// 按单个路径排序。
    ///
    /// @param path 路径
    /// @return OrderOperator 实例
    default OrderOperator<T, U> orderBy(PathRef<T, ? extends Comparable<?>> path) {
        return orderBy(List.of(path));
    }

    /// 按两个路径排序。
    ///
    /// @param p1 第一个路径
    /// @param p2 第二个路径
    /// @return OrderOperator 实例
    default OrderOperator<T, U> orderBy(PathRef<T, ? extends Comparable<?>> p1, PathRef<T, ? extends Comparable<?>> p2) {
        return orderBy(List.of(p1, p2));
    }

    /// 按三个路径排序。
    ///
    /// @param p1 第一个路径
    /// @param p2 第二个路径
    /// @param p3 第三个路径
    /// @return OrderOperator 实例
    default OrderOperator<T, U> orderBy(PathRef<T, ? extends Comparable<?>> p1, PathRef<T, ? extends Comparable<?>> p2, PathRef<T, ? extends Comparable<?>> p3) {
        return orderBy(List.of(p1, p2, p3));
    }

}