package io.github.nextentity.api;

/// 路径表达式接口，表示实体属性的路径表达式。
///
/// 继承 SimpleExpression，提供基本的表达式操作方法。
/// 同时继承 PathRef，允许 Path 实例在查询构建方法中作为 PathRef 参数传递。
///
/// ## 使用示例
///
/// ```java
/// // 使用方法引用创建路径
/// Path<User, String> namePath = Path.of(User::getName);
///
/// // 在查询中使用
/// repository.query()
///     .where(Path.of(User::getName)).eq("张三")
///     .getList();
/// ```
///
/// @param <T> 实体类型
/// @param <U> 表达式值类型
/// @author HuangChengwei
/// @since 1.0.0
public interface Path<T, U> extends SimpleExpression<T, U>, PathRef<T, U> {

    /// 从指定路径引用创建路径表达式。
    ///
    /// @param path 路径引用
    /// @param <T>  实体类型
    /// @param <U>  值类型
    /// @return 路径表达式
    static <T, U> Path<T, U> of(PathRef<T, U> path) {
        return EntityRoot.<T>of().get(path);
    }

    /// 从指定布尔引用创建布尔路径。
    ///
    /// @param path 布尔引用
    /// @param <T> 实体类型
    /// @return 布尔路径
    static <T> BooleanPath<T> of(PathRef.BooleanRef<T> path) {
        return BooleanPath.of(path);
    }

    /// 从指定数值引用创建数值路径。
    ///
    /// @param path 数值引用
    /// @param <T> 实体类型
    /// @param <U> 数值类型
    /// @return 数值路径
    static <T, U extends Number> NumberPath<T, U> of(PathRef.NumberRef<T, U> path) {
        return NumberPath.of(path);
    }

    /// 从指定字符串引用创建字符串路径。
    ///
    /// @param path 字符串引用
    /// @param <T> 实体类型
    /// @return 字符串路径
    static <T> StringPath<T> of(PathRef.StringRef<T> path) {
        return StringPath.of(path);
    }

    /// 从指定实体路径引用创建实体路径。
    ///
    /// @param path 实体路径引用
    /// @param <T>  实体类型
    /// @param <U>  嵌套实体类型（必须实现 Entity）
    /// @return 实体路径
    static <T, U extends Entity> EntityPath<T, U> of(PathRef.EntityPathRef<T, U> path) {
        return EntityPath.of(path);
    }

    // type-unsafe

    /// 从指定字段名创建路径表达式（类型不安全）。
    ///
    /// @param path 字段名
    /// @param <T> 实体类型
    /// @param <U> 值类型
    /// @return 路径表达式
    static <T, U> Path<T, U> of(String path) {
        return EntityRoot.<T>of().path(path);
    }

    /// PathRef 中 apply 方法的默认实现。
    ///
    /// 此方法不打算被子类调用或实现。
    /// 它存在的唯一原因是 Path 继承了 PathRef，
    /// 允许 Path 实例在查询构建方法中作为 PathRef 参数传递。
    ///
    /// @param t 实体对象（未使用）
    /// @return 正常情况下不会返回
    /// @throws UnsupportedOperationException 始终抛出
    @Override
    default U apply(T t) {
        throw new UnsupportedOperationException();
    }

}