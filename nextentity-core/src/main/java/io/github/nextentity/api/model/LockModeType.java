package io.github.nextentity.api.model;

import java.io.Serializable;

/**
 * Lock mode type enum, defining different locking strategies.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public enum LockModeType implements Serializable {

    /**
     * Read lock mode.
     */
    READ,
    /**
     * Write lock mode.
     */
    WRITE,
    /**
     * Optimistic lock mode.
     */
    OPTIMISTIC,
    /**
     * Optimistic lock force increment mode.
     */
    OPTIMISTIC_FORCE_INCREMENT,
    /**
     * Pessimistic read lock mode.
     */
    PESSIMISTIC_READ,
    /**
     * Pessimistic write lock mode.
     */
    PESSIMISTIC_WRITE,
    /**
     * Pessimistic lock force increment mode.
     */
    PESSIMISTIC_FORCE_INCREMENT,
    /**
     * No lock mode.
     */
    NONE

}
