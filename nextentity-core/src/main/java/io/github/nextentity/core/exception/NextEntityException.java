package io.github.nextentity.core.exception;

/// NextEntity框架所有异常的基类。
///
/// 所有框架特定的异常都应继承此类。
///
/// @since 2.0.0
public class NextEntityException extends RuntimeException {

    /// 使用指定的详细消息构造新异常。
    ///
    /// @param message 详细消息
    public NextEntityException(String message) {
        super(message);
    }

    /// 使用指定的详细消息和原因构造新异常。
    ///
    /// @param message 详细消息
    /// @param cause   此异常的原因
    public NextEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    /// 使用指定的原因构造新异常。
    ///
    /// @param cause 此异常的原因
    public NextEntityException(Throwable cause) {
        super(cause);
    }

}