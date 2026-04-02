package io.github.nextentity.api;

/// 类型化表达式接口，表示具有特定值类型的表达式。
///
/// ## 构建表达式实例
///
/// 表达式实例可以通过子接口提供的 `of` 静态方法创建：
///
/// - **`Path.of(PathRef)`** - 从方法引用创建路径表达式（如 `User::getName`）
/// - **`BooleanPath.of(PathRef.BooleanRef)`** - 创建布尔路径表达式
/// - **`NumberPath.of(PathRef.NumberRef)`** - 创建数值路径表达式
/// - **`StringPath.of(PathRef.StringRef)`** - 创建字符串路径表达式
/// - **`EntityPath.of(PathRef)`** - 创建实体路径表达式用于嵌套实体访问
/// - **`Expression.of(value)`** - 从值创建字面量表达式
///
/// ## 使用示例
///
/// ```java
/// // 通过方法引用创建路径表达式
/// Path<User, String> path = Path.of(User::getName);
///
/// // 字符串特定的路径
/// StringPath<User> stringPath = StringPath.of(User::getName);
///
/// // 数值路径
/// NumberPath<User, Integer> agePath = NumberPath.of(User::getAge);
///
/// // 字面量表达式
/// Expression<User, String> literal = Expression.of("John");
/// ```
///
/// @param <T> 实体类型
/// @param <U> 表达式值类型
/// @author HuangChengwei
/// @since 1.0.0
@SuppressWarnings("unused")
public interface Expression<T, U> {
    /// 从指定值创建类型化表达式。
    ///
    /// @param value 字面量值
    /// @param <T>   实体类型
    /// @param <U>   值类型
    /// @return 类型化表达式
    static <T, U> Expression<T, U> of(U value) {
        return EntityRoot.<T>of().literal(value);
    }
}