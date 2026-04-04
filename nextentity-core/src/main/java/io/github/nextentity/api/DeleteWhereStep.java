package io.github.nextentity.api;

/// 条件删除构建器接口，用于带 WHERE 条件的批量删除操作。
///
/// 该接口提供流畅的 API 用于构建带有条件 WHERE 子句的 DELETE 语句，
/// 允许在不先获取实体的情况下执行批量删除。
///
/// 使用示例：
/// <pre>{@code
/// // 删除所有不活跃用户
/// int deleted = repository.delete()
///     .where(User::getStatus).eq("INACTIVE")
///     .execute();
///
/// // 多条件删除
/// int deleted = repository.delete()
///     .where(User::getStatus).eq("ARCHIVED")
///     .and(User::getCreatedAt).lt(oneYearAgo)
///     .execute();
///
/// // 使用谓词表达式删除
/// int deleted = repository.delete()
///     .where(root().get(User::status).eq("INACTIVE")
///         .and(root().get(User::lastLoginAt).lt(threshold)))
///     .execute();
/// }</pre>
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.0.0
public interface DeleteWhereStep<T> extends Whereable<T, DeleteWhereStep<T>>, Executable {

}