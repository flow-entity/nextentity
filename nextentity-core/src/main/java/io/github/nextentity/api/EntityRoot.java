package io.github.nextentity.api;

import io.github.nextentity.core.util.DefaultEntityRoot;

/// 实体根接口，提供实体属性访问和路径构建方法。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @see Path 路径表达式创建和使用示例
/// @since 1.0.0
public interface EntityRoot<T> {

    /// 创建实体根实例。
    ///
    /// @param <T> 实体类型
    /// @return 实体根实例
    static <T> EntityRoot<T> of() {
        return DefaultEntityRoot.of();
    }

    /// 创建字面量表达式。
    ///
    /// @param value 字面量值
    /// @param <U>   字面量类型
    /// @return 字面量表达式
    <U> Expression<T, U> literal(U value);

    /// 获取指定路径的实体路径表达式。
    ///
    /// @param path 属性路径
    /// @param <U>  属性类型
    /// @return 实体路径表达式
    <U> EntityPath<T, U> get(PathRef<T, U> path);

    /// 获取指定布尔属性路径的布尔路径表达式。
    ///
    /// @param path 布尔属性路径
    /// @return 布尔路径表达式
    BooleanPath<T> get(PathRef.BooleanRef<T> path);

    /// 获取指定字符串属性路径的字符串路径表达式。
    ///
    /// @param path 字符串属性路径
    /// @return 字符串路径表达式
    StringPath<T> get(PathRef.StringRef<T> path);

    /// 获取指定数值属性路径的数值路径表达式。
    ///
    /// @param path 数值属性路径
    /// @param <U>  数值类型
    /// @return 数值路径表达式
    <U extends Number> NumberPath<T, U> get(PathRef.NumberRef<T, U> path);

    /// 创建指定路径的路径表达式。
    ///
    /// @param path 属性路径
    /// @param <U>  属性类型
    /// @return 路径表达式
    <U> Path<T, U> path(PathRef<T, U> path);

    /// 创建指定路径的实体路径表达式。
    ///
    /// @param path 属性路径
    /// @param <U>  属性类型
    /// @return 实体路径表达式
    <U> EntityPath<T, U> entity(PathRef<T, U> path);

    /// 创建指定字符串路径的字符串路径表达式。
    ///
    /// @param path 字符串属性路径
    /// @return 字符串路径表达式
    StringPath<T> string(PathRef<T, String> path);

    /// 创建指定数值路径的数值路径表达式。
    ///
    /// @param path 数值属性路径
    /// @param <U>  数值类型
    /// @return 数值路径表达式
    <U extends Number> NumberPath<T, U> number(PathRef<T, U> path);

    /// 创建指定布尔路径的布尔路径表达式。
    ///
    /// @param path 布尔属性路径
    /// @return 布尔路径表达式
    BooleanPath<T> bool(PathRef<T, Boolean> path);

    // type-unsafe

    /// 通过字段名创建路径表达式（类型不安全）。
    ///
    /// @param fieldName 字段名
    /// @param <U>       属性类型
    /// @return 路径表达式
    <U> Path<T, U> path(String fieldName);

    /// 通过字段名创建实体路径表达式（类型不安全）。
    ///
    /// @param fieldName 字段名
    /// @param <U>       属性类型
    /// @return 实体路径表达式
    <U> EntityPath<T, U> entityPath(String fieldName);

    /// 通过字段名创建字符串路径表达式（类型不安全）。
    ///
    /// @param fieldName 字段名
    /// @return 字符串路径表达式
    StringPath<T> stringPath(String fieldName);

    /// 通过字段名创建数值路径表达式（类型不安全）。
    ///
    /// @param fieldName 字段名
    /// @param <U>       数值类型
    /// @return 数值路径表达式
    <U extends Number> NumberPath<T, U> numberPath(String fieldName);

    /// 通过字段名创建布尔路径表达式（类型不安全）。
    ///
    /// @param fieldName 字段名
    /// @return 布尔路径表达式
    BooleanPath<T> booleanPath(String fieldName);

}