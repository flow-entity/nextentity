package io.github.nextentity.api;

/// 布尔路径接口，表示实体布尔类型属性的路径。
///
/// 同时继承 PathRef.BooleanRef，允许 BooleanPath 实例在查询构建方法中
/// 作为 BooleanRef 参数传递。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @see Path 路径表达式创建和使用示例
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