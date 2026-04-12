package io.github.nextentity.api;

/// 数值路径接口，表示实体数值类型属性的路径。
///
/// 同时继承 PathRef.NumberRef，允许 NumberPath 实例在查询构建方法中
/// 作为 NumberRef 参数传递。
///
/// @param <T> 实体类型
/// @param <U> 数值类型
/// @author HuangChengwei
/// @see Path 路径表达式创建和使用示例
/// @see NumberExpression 数值运算方法
/// @since 1.0.0
public interface NumberPath<T, U extends Number> extends NumberExpression<T, U>, Path<T, U>, PathRef.NumberRef<T, U> {
    /// 从指定数值引用创建数值路径。
    ///
    /// @param path 数值引用
    /// @param <T>  实体类型
    /// @param <U>  数值类型
    /// @return 数值路径
    static <T, U extends Number> NumberPath<T, U> of(PathRef.NumberRef<T, U> path) {
        return EntityRoot.<T>of().get(path);
    }
}