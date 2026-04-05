package io.github.nextentity.core.exception;

/// 乐观锁冲突时抛出的异常。
///
/// 通常在实体被另一个事务在加载和更新之间修改时发生。
///
/// @since 1.0.0
public class OptimisticLockException extends NextEntityException {

    /// 使用指定的详细消息构造新异常。
    ///
    /// @param message 详细消息
    public OptimisticLockException(String message) {
        super(message);
    }

    /// 使用指定的详细消息和原因构造新异常。
    ///
    /// @param message 详细消息
    /// @param cause   此异常的原因
    public OptimisticLockException(String message, Throwable cause) {
        super(message, cause);
    }

}