package io.github.nextentity.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

///
/// 数据库连接提供者接口
///
/// 该接口定义了数据库连接的获取和执行方法，用于统一管理数据库连接的生命周期和事务控制。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface ConnectionProvider {

    /// 在数据库连接上执行操作
    ///
    /// @param <T> 操作返回结果类型
    /// @param action 要执行的操作
    /// @return 操作结果
    /// @throws SQLException SQL异常
    <T> T execute(ConnectionCallback<T> action) throws SQLException;

    /// 在事务中执行操作
    ///
    /// @param <T> 操作返回结果类型
    /// @param action 要执行的操作
    /// @return 操作结果
    /// @throws SQLException SQL异常
    <T> T executeInTransaction(ConnectionCallback<T> action) throws SQLException;

    /// 数据库连接回调接口
    ///
    /// 用于在给定的数据库连接上执行具体操作
    interface ConnectionCallback<T> {
        /// 在数据库连接上执行操作
        ///
        /// @param connection 数据库连接
        /// @return 操作结果
        /// @throws SQLException SQL异常
        T doInConnection(Connection connection) throws SQLException;
    }
}
