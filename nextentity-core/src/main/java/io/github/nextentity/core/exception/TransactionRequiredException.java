package io.github.nextentity.core.exception;

/// 需要事务但事务未激活时抛出的异常。
///
/// 通常在非事务上下文中执行数据库操作时发生。
///
/// @since 1.0.0
public class TransactionRequiredException extends NextEntityException {

    /// 使用默认消息构造新异常。
    public TransactionRequiredException() {
        super("Transaction required but not active. Please ensure the operation is executed within a transaction context.");
    }

    /// 使用指定的详细消息构造新异常。
    ///
    /// @param message 详细消息
    public TransactionRequiredException(String message) {
        super(message);
    }

    /// 使用指定的详细消息和原因构造新异常。
    ///
    /// @param message 详细消息
    /// @param cause   此异常的原因
    public TransactionRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    /// 使用指定的原因构造新异常。
    ///
    /// @param cause 此异常的原因
    public TransactionRequiredException(Throwable cause) {
        super(cause);
    }

}