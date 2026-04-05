package io.github.nextentity.core.exception;

/// 反射操作失败时抛出的异常。
///
/// 包括 Bean 属性访问、方法调用以及其他反射操作失败的情况。
///
/// @since 2.0.0
public class ReflectiveException extends NextEntityException {

    /// 使用指定的详细消息构造新异常。
    ///
    /// @param message 详细消息
    public ReflectiveException(String message) {
        super(message);
    }

    /// 使用指定的详细消息和原因构造新异常。
    ///
    /// @param message 详细消息
    /// @param cause   此异常的原因
    public ReflectiveException(String message, Throwable cause) {
        super(message, cause);
    }

    /// 使用指定的原因构造新异常。
    ///
    /// @param cause 此异常的原因
    public ReflectiveException(Throwable cause) {
        super(cause);
    }

}