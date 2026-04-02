package io.github.nextentity.api;

import org.jspecify.annotations.NonNull;

/// 条件删除构建器接口，用于带 WHERE 条件的批量删除操作。
///
/// 该接口提供流畅的 API 用于构建带有条件 WHERE 子句的 DELETE 语句，
/// 允许在不先获取实体的情况下执行批量删除。
///
/// 使用示例：
/// <pre>{@code
/// // 删除所有不活跃用户
/// int deleted = repository.deleteWhere()
///     .where(User::getStatus).eq("INACTIVE")
///     .execute();
///
/// // 多条件删除
/// int deleted = repository.deleteWhere()
///     .where(User::getStatus).eq("ARCHIVED")
///     .and(User::getCreatedAt).lt(oneYearAgo)
///     .execute();
///
/// // 使用谓词表达式删除
/// int deleted = repository.deleteWhere()
///     .where(root().get(User::status).eq("INACTIVE")
///         .and(root().get(User::lastLoginAt).lt(threshold)))
///     .execute();
/// }</pre>
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1
public interface DeleteWhereStep<T> {

    /// 为指定路径开始 WHERE 条件。
    ///
    /// @param path 路径引用，用于开始条件
    /// @param <N>  路径值类型
    /// @return 路径操作器，用于构建条件
    <N> ExpressionBuilder.PathOperator<T, N, ? extends DeleteWhereStep<T>> where(PathRef<T, N> path);

    /// 为指定数字路径开始 WHERE 条件。
    ///
    /// @param path 数字路径引用
    /// @param <N>  数字类型
    /// @return 数字操作器，用于构建条件
    <N extends Number> ExpressionBuilder.NumberOperator<T, N, ? extends DeleteWhereStep<T>> where(PathRef.NumberRef<T, N> path);

    /// 为指定字符串路径开始 WHERE 条件。
    ///
    /// @param path 字符串路径引用
    /// @return 字符串操作器，用于构建条件
    ExpressionBuilder.StringOperator<T, ? extends DeleteWhereStep<T>> where(PathRef.StringRef<T> path);

    /// 使用谓词表达式添加 WHERE 条件。
    ///
    /// @param predicate 谓词表达式
    /// @return 当前构建器，用于方法链式调用
    DeleteWhereStep<T> where(@NonNull Expression<T, Boolean> predicate);

    /// 执行删除语句。
    ///
    /// @return 受影响的行数
    int execute();
}