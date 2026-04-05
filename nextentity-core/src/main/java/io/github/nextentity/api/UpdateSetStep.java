package io.github.nextentity.api;

/// 条件更新构建器接口，用于带 WHERE 条件的批量更新操作。
///
/// 该接口提供流畅的 API 用于构建带有条件 WHERE 子句的 UPDATE 语句，
/// 允许在不先获取实体的情况下执行批量更新。
///
/// 使用示例：
/// <pre>{@code
/// // 将所有不活跃用户更新为归档状态
/// int updated = repository.update()
///     .set(User::getStatus, "ARCHIVED")
///     .where(User::getLastLoginAt).lt(threshold)
///     .execute();
///
/// // 多条件更新
/// int updated = repository.update()
///     .set(User::getStatus, "INACTIVE")
///     .set(User::updatedAt, LocalDateTime.now())
///     .where(User::getStatus).eq("ACTIVE")
///     .and(User::getLastLoginAt).lt(oneYearAgo)
///     .execute();
/// }</pre>
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.0.0
public interface UpdateSetStep<T> extends UpdateWhereStep<T> {

    /// 设置指定字段的值。
    ///
    /// @param path  字段路径引用
    /// @param value 要设置的值
    /// @param <U>   字段值类型
    /// @return 当前构建器，用于方法链式调用
    <U> UpdateSetStep<T> set(PathRef<T, U> path, U value);

}