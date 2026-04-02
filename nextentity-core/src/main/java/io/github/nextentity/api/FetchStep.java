package io.github.nextentity.api;

import java.util.Collection;
import java.util.List;

/// 预加载步骤接口，提供预加载关联数据的方法。
///
/// 继承 WhereStep，用于指定需要预加载的关联数据。
/// 预加载可以避免 N+1 查询问题。
///
/// ## 使用示例
///
/// ```java
/// // 预加载单个关联
/// List<User> users = repository.query()
///     .fetch(User::getDepartment)
///     .where(User::getStatus).eq("ACTIVE")
///     .getList();
///
/// // 预加载多个关联
/// List<User> users = repository.query()
///     .fetch(User::getDepartment, User::getRole)
///     .getList();
///
/// // 预加载嵌套关联
/// List<User> users = repository.query()
///     .fetch(User::getDepartment)
///     .fetch(Path.of(User::getDepartment).get(Department::getManager))
///     .getList();
/// ```
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 1.0.0
public interface FetchStep<T> extends BaseWhereStep<T, T> {

    /// 预加载指定路径表达式列表对应的关联数据。
    ///
    /// @param expressions 路径表达式列表
    /// @return WhereStep 实例
    BaseWhereStep<T, T> fetch(Collection<? extends PathRef<T, ?>> expressions);

    /// 预加载指定路径表达式对应的关联数据。
    ///
    /// @param path 路径表达式
    /// @return WhereStep 实例
    default BaseWhereStep<T, T> fetch(Path<T, ?> path) {
        return fetch(List.<Path<T, ?>>of(path));
    }

    /// 预加载指定两个路径表达式对应的关联数据。
    ///
    /// @param p0 第一个路径表达式
    /// @param p1 第二个路径表达式
    /// @return WhereStep 实例
    default BaseWhereStep<T, T> fetch(Path<T, ?> p0, Path<T, ?> p1) {
        return fetch(List.<Path<T, ?>>of(p0, p1));
    }

    /// 预加载指定三个路径表达式对应的关联数据。
    ///
    /// @param p0 第一个路径表达式
    /// @param p1 第二个路径表达式
    /// @param p3 第三个路径表达式
    /// @return WhereStep 实例
    default BaseWhereStep<T, T> fetch(Path<T, ?> p0, Path<T, ?> p1, Path<T, ?> p3) {
        return fetch(List.<Path<T, ?>>of(p0, p1, p3));
    }

    /// 预加载指定路径对应的关联数据。
    ///
    /// @param path 路径
    /// @return WhereStep 实例
    default BaseWhereStep<T, T> fetch(PathRef<T, ?> path) {
        return fetch(Path.of(path));
    }

    /// 预加载指定两个路径对应的关联数据。
    ///
    /// @param p0 第一个路径
    /// @param p1 第二个路径
    /// @return WhereStep 实例
    default BaseWhereStep<T, T> fetch(PathRef<T, ?> p0, PathRef<T, ?> p1) {
        return fetch(Path.of(p0), Path.of(p1));
    }

    /// 预加载指定三个路径对应的关联数据。
    ///
    /// @param p0 第一个路径
    /// @param p1 第二个路径
    /// @param p3 第三个路径
    /// @return WhereStep 实例
    default BaseWhereStep<T, T> fetch(PathRef<T, ?> p0, PathRef<T, ?> p1, PathRef<T, ?> p3) {
        return fetch(Path.of(p0), Path.of(p1), Path.of(p3));
    }

}