package io.github.nextentity.api;

import io.github.nextentity.api.model.Order;
import io.github.nextentity.api.model.Slice;
import jakarta.persistence.LockModeType;

import java.util.Collection;
import java.util.List;

/// 排序操作器接口，提供排序相关的操作方法。
///
/// 继承 OrderByStep，提供升序、降序等排序操作。
///
/// ## 使用示例
///
/// ```java
/// // 升序排序
/// List<User> users = repository.query()
///     .orderBy(User::getName).asc()
///     .getList();
///
/// // 降序排序
/// List<User> users = repository.query()
///     .orderBy(User::getCreateTime).desc()
///     .getList();
///
/// // 指定排序方向
/// List<User> users = repository.query()
///     .orderBy(User::getName).sort(SortOrder.DESC)
///     .getList();
///
/// // 多字段排序
/// List<User> users = repository.query()
///     .orderBy(User::getDepartment).asc()
///     .orderBy(User::getName).desc()
///     .getList();
/// ```
///
/// @param <T> 实体类型
/// @param <U> 结果类型
/// @author HuangChengwei
/// @since 1.0.0
public interface OrderOperator<T, U> extends OrderByStep<T, U> {
    /// 升序排序。
    ///
    /// @return OrderByStep 实例
    default OrderByStep<T, U> asc() {
        return sort(SortOrder.ASC);
    }

    /// 降序排序。
    ///
    /// @return OrderByStep 实例
    default OrderByStep<T, U> desc() {
        return sort(SortOrder.DESC);
    }

    /// 按指定的排序方向排序。
    ///
    /// @param order 排序方向
    /// @return OrderByStep 实例
    OrderByStep<T, U> sort(SortOrder order);

    /// 按指定的路径集合排序。
    ///
    /// @param paths 路径集合
    /// @return OrderOperator 实例
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
    default boolean exists(int offset) {
        return asc().exists(offset);
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
    default List<U> list(int limit) {
        return asc().list(limit);
    }

    @Override
    default List<U> list(int offset, int limit) {
        return asc().list(offset, limit);
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

    /// 获取指定偏移量和最大结果数的列表，并指定锁定模式。
    ///
    /// @param offset       偏移量
    /// @param maxResult    最大结果数
    /// @param lockModeType 锁定模式
    /// @return 结果列表
    /// @deprecated 已废弃，请使用 {@link #asc()} 或 {@link #desc()} 后调用相应方法代替。
    @Override
    @Deprecated
    default List<U> getList(int offset, int maxResult, LockModeType lockModeType) {
        return asc().getList(offset, maxResult, lockModeType);
    }
}