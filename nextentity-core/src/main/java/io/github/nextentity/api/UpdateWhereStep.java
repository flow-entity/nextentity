package io.github.nextentity.api;

import org.jspecify.annotations.NonNull;

/// 条件更新构建器接口，用于带 WHERE 条件的批量更新操作。
///
/// 该接口提供流畅的 API 用于构建带有条件 WHERE 子句的 UPDATE 语句，
/// 允许在不先获取实体的情况下执行批量更新。
///
/// 使用示例：
/// <pre>{@code
/// // 将所有不活跃用户更新为归档状态
/// int updated = repository.updateWhere()
///     .set(User::getStatus, "ARCHIVED")
///     .where(User::getLastLoginAt).lt(threshold)
///     .execute();
///
/// // 多条件更新
/// int updated = repository.updateWhere()
///     .set(User::getStatus, "INACTIVE")
///     .set(User::updatedAt, LocalDateTime.now())
///     .where(User::getStatus).eq("ACTIVE")
///     .and(User::getLastLoginAt).lt(oneYearAgo)
///     .execute();
/// }</pre>
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1
public interface UpdateWhereStep<T> {

    /// 设置指定字段的值。
    ///
    /// @param path  字段路径引用
    /// @param value 要设置的值
    /// @param <U>   字段值类型
    /// @return 当前构建器，用于方法链式调用
    <U> UpdateWhereStep<T> set(PathRef<T, U> path, U value);

    /// 通过字段名设置值（非类型安全）。
    ///
    /// @param fieldName 字段名
    /// @param value     要设置的值
    /// @return 当前构建器，用于方法链式调用
    UpdateWhereStep<T> set(String fieldName, Object value);

    /// 为指定路径开始 WHERE 条件。
    ///
    /// @param path 路径引用，用于开始条件
    /// @param <N>  路径值类型
    /// @return 路径操作器，用于构建条件
    <N> ExpressionBuilder.PathOperator<T, N, ? extends UpdateWhereStep<T>> where(PathRef<T, N> path);

    /// 为指定数字路径开始 WHERE 条件。
    ///
    /// @param path 数字路径引用
    /// @param <N>  数字类型
    /// @return 数字操作器，用于构建条件
    <N extends Number> ExpressionBuilder.NumberOperator<T, N, ? extends UpdateWhereStep<T>> where(PathRef.NumberRef<T, N> path);

    /// 为指定字符串路径开始 WHERE 条件。
    ///
    /// @param path 字符串路径引用
    /// @return 字符串操作器，用于构建条件
    ExpressionBuilder.StringOperator<T, ? extends UpdateWhereStep<T>> where(PathRef.StringRef<T> path);

    /// 使用谓词表达式添加 WHERE 条件。
    ///
    /// @param predicate 谓词表达式
    /// @return 当前构建器，用于方法链式调用
    UpdateWhereStep<T> where(@NonNull Expression<T, Boolean> predicate);

    /// 执行更新语句。
    ///
    /// @return 受影响的行数
    int execute();
}