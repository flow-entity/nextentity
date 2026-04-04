package io.github.nextentity.api;

/// 可执行操作接口，用于批量数据库操作。
///
/// 该接口定义了执行批量操作并返回受影响行数的方法，
/// 适用于 DELETE、UPDATE 等不需要返回实体数据的批量操作。
///
/// 使用示例：
/// <pre>{@code
/// // 执行批量更新
/// int updated = repository.update()
///     .set(User::getStatus, "INACTIVE")
///     .where(User::getLastLoginAt).lt(threshold)
///     .execute();
///
/// // 执行批量删除
/// int deleted = repository.delete()
///     .where(User::getStatus).eq("ARCHIVED")
///     .execute();
/// }</pre>
///
/// @author HuangChengwei
/// @since 2.0.0
public interface Executable {

    /// 执行批量操作。
    ///
    /// 执行构建的 DELETE 或 UPDATE 语句，
    /// 返回数据库中受影响的行数。
    ///
    /// @return 受影响的行数
    int execute();
}