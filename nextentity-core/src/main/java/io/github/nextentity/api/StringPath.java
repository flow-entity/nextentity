package io.github.nextentity.api;

/// 字符串路径接口，表示实体字符串类型属性的路径。
///
/// 同时继承 PathRef.StringRef，允许 StringPath 实例在查询构建方法中
/// 作为 StringRef 参数传递。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @see Path 路径表达式创建和使用示例
/// @since 1.0.0
public interface StringPath<T> extends StringExpression<T>, Path<T, String>, PathRef.StringRef<T> {
    /// 从指定字符串引用创建字符串路径。
    ///
    /// @param path 字符串引用
    /// @param <T>  实体类型
    /// @return 字符串路径
    static <T> StringPath<T> of(PathRef.StringRef<T> path) {
        return EntityRoot.<T>of().get(path);
    }
}