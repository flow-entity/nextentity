package io.github.nextentity.api;

import jakarta.persistence.LockModeType;

/// 支持在收集结果前应用锁模式的查询步骤。
///
/// ## 使用示例
///
/// ```java
/// // 悲观读锁
/// User user = repository.query()
///     .where(User::getId).eq(1L)
///     .lock(LockModeType.PESSIMISTIC_READ)
///     .first();
///
/// // 悲观写锁
/// User user = repository.query()
///     .where(User::getId).eq(1L)
///     .lock(LockModeType.PESSIMISTIC_WRITE)
///     .first();
/// ```
///
/// @param <T> 结果类型
/// @author HuangChengwei
/// @since 1.0.0
public interface LockStep<T> extends Collector<T> {

    /// 设置查询的锁模式。
    ///
    /// @param lockModeType 锁模式类型
    /// @return 带锁的收集器视图
    Collector<T> lock(LockModeType lockModeType);
}