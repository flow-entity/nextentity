package io.github.nextentity.api;

import io.github.nextentity.core.util.DefaultEntityRoot;

/// 实体路径接口，表示实体之间的关联路径。
///
/// 继承 PathExpression，用于处理实体之间的关联属性访问。
///
/// ## 使用示例
///
/// ```java
/// // 访问关联对象的属性
/// EntityPath<User, Department> deptPath = EntityPath.of(User::getDepartment);
/// StringPath<User> deptName = deptPath.get(Department::getName);
///
/// // 在查询中使用嵌套属性
/// repository.query()
///     .where(User::getDepartment).get(Department::getName).eq("技术部")
///     .getList();
/// ```
///
/// @param <T> 实体类型
/// @param <U> 属性类型
/// @author HuangChengwei
/// @since 1.0.0
public interface EntityPath<T, U> extends Path<T, U> {

    /// 从指定路径引用创建实体路径。
    ///
    /// @param path 路径引用
    /// @param <T>  实体类型
    /// @param <U>  属性类型
    /// @return 实体路径
    static <T, U> EntityPath<T, U> of(PathRef<T, U> path) {
        return DefaultEntityRoot.<T>of().entity(path);
    }

    // type-unsafe

    /// 从指定字段名创建实体路径（类型不安全）。
    ///
    /// @param path 字段名
    /// @param <T> 实体类型
    /// @param <U> 属性类型
    /// @return 实体路径
    static <T, U> EntityPath<T, U> of(String path) {
        return DefaultEntityRoot.<T>of().entityPath(path);
    }


    /// 获取指定路径的子路径。
    ///
    /// @param path 路径
    /// @param <R> 结果类型
    /// @return 子路径
    <R> EntityPath<T, R> get(PathRef<U, R> path);

    /// 获取指定字符串引用的字符串路径。
    ///
    /// @param path 字符串引用
    /// @return 字符串路径
    StringPath<T> get(PathRef.StringRef<U> path);

    /// 获取指定数值引用的数值路径。
    ///
    /// @param path 数值引用
    /// @param <R> 数值类型
    /// @return 数值路径
    <R extends Number> NumberPath<T, R> get(PathRef.NumberRef<U, R> path);

    /// 获取指定路径表达式的子路径表达式。
    ///
    /// @param path 路径表达式
    /// @param <R> 结果类型
    /// @return 子路径表达式
    <R> Path<T, R> get(Path<U, R> path);

    /// 获取指定字符串路径的字符串路径。
    ///
    /// @param path 字符串路径
    /// @return 字符串路径
    StringPath<T> get(StringPath<U> path);

    /// 获取指定布尔引用的布尔路径。
    ///
    /// @param path 布尔引用
    /// @return 布尔路径
    BooleanPath<T> get(PathRef.BooleanRef<T> path);

    /// 获取指定数值路径的数值路径。
    ///
    /// @param path 数值路径
    /// @param <R> 数值类型
    /// @return 数值路径
    <R extends Number> NumberPath<T, R> get(NumberPath<U, R> path);

}