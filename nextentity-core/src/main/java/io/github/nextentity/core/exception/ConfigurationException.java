package io.github.nextentity.core.exception;

/// 配置错误时抛出的异常。
///
/// 通常在无法从泛型参数解析实体类型或存储库配置不正确时发生。
///
/// @since 2.0.0
public class ConfigurationException extends NextEntityException {

    /// 使用指定的详细消息构造新异常。
    ///
    /// @param message 详细消息
    public ConfigurationException(String message) {
        super(message);
    }

    /// 使用指定的详细消息和原因构造新异常。
    ///
    /// @param message 详细消息
    /// @param cause   此异常的原因
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /// 使用指定的原因构造新异常。
    ///
    /// @param cause 此异常的原因
    public ConfigurationException(Throwable cause) {
        super(cause);
    }

}