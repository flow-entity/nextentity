package io.github.nextentity.api;

/// 可执行操作接口，用于批量数据库操作。
///
/// 定义执行批量 DELETE/UPDATE 操作并返回受影响行数的方法。
///
/// @author HuangChengwei
/// @see UpdateSetStep 条件更新示例
/// @see DeleteWhereStep 条件删除示例
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