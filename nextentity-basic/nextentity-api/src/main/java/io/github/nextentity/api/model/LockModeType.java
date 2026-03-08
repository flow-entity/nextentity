package io.github.nextentity.api.model;

import java.io.Serializable;

/**
 * 锁模式类型枚举，定义了不同的锁定策略。
 *
 * @author HuangChengwei
 * @since 2026/1/7
 */
public enum LockModeType implements Serializable {

    /**
     * 读锁模式。
     */
    READ,
    /**
     * 写锁模式。
     */
    WRITE,
    /**
     * 乐观锁模式。
     */
    OPTIMISTIC,
    /**
     * 乐观锁强制增量模式。
     */
    OPTIMISTIC_FORCE_INCREMENT,
    /**
     * 悲观读锁模式。
     */
    PESSIMISTIC_READ,
    /**
     * 悲观写锁模式。
     */
    PESSIMISTIC_WRITE,
    /**
     * 悲观锁强制增量模式。
     */
    PESSIMISTIC_FORCE_INCREMENT,
    /**
     * 无锁模式。
     */
    NONE

}
