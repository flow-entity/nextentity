package io.github.nextentity.api;

/// 类型化表达式接口，表示具有特定值类型的表达式。
///
/// @param <T> 实体类型
/// @param <U> 表达式值类型
/// @author HuangChengwei
/// @see Path 路径表达式创建示例
/// @see SimpleExpression 基本表达式操作示例
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