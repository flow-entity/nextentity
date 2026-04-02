package io.github.nextentity.api;

/// 布尔路径接口，表示实体布尔类型属性的路径。
///
/// 同时继承 PathRef.BooleanRef，允许 BooleanPath 实例在查询构建方法中
/// 作为 BooleanRef 参数传递。
///
/// ## 使用示例
///
/// ```java
/// // 创建布尔路径
/// BooleanPath<User> activePath = BooleanPath.of(User::isActive);
///
/// // 在查询中使用
/// List<User> activeUsers = repository.query()
///     .where(BooleanPath.of(User::isActive)).eq(true)
///     .getList();
///
/// // 直接作为断言使用
/// repository.query()
///     .where(User::isActive)
///     .getList();
/// ```
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 1.0.0
public interface BooleanPath<T> extends Predicate<T>, Path<T, Boolean>, PathRef.BooleanRef<T> {
    /// 从指定布尔引用创建布尔路径。
    ///
    /// @param path 布尔引用
    /// @param <T>  实体类型
    /// @return 布尔路径
    static <T> BooleanPath<T> of(PathRef.BooleanRef<T> path) {
        return EntityRoot.<T>of().get(path);
    }
}