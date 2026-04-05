package io.github.nextentity.core.exception;

import java.sql.SQLException;

/// {@link SQLException} 的非检查型包装类。
///
/// 提供导致异常的 SQL 相关的额外上下文信息。
///
/// @since 2.0.0
public class SqlException extends NextEntityException {

    /// 使用指定的详细消息构造新异常。
    ///
    /// @param message 详细消息
    public SqlException(String message) {
        super(message);
    }

    /// 使用指定的详细消息和原因构造新异常。
    ///
    /// @param message 详细消息
    /// @param cause   此异常的原因
    public SqlException(String message, Throwable cause) {
        super(message, cause);
    }

    /// 使用指定的原因构造新异常。
    ///
    /// @param cause 此异常的原因
    public SqlException(Throwable cause) {
        super(cause);
    }

}