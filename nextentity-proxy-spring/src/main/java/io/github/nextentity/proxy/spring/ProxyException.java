package io.github.nextentity.proxy.spring;

/// 代理异常
///
/// 当无法创建代理时抛出此异常。
public class ProxyException extends RuntimeException {

    /// 创建代理异常
    ///
    /// @param message 异常消息
    public ProxyException(String message) {
        super(message);
    }

    /// 创建代理异常
    ///
    /// @param message 异常消息
    /// @param cause   原因异常
    public ProxyException(String message, Throwable cause) {
        super(message, cause);
    }
}