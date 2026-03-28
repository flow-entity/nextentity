package io.github.nextentity.core.exception;

/**
 * Exception thrown when an optimistic locking conflict occurs.
 * This typically happens when an entity is modified by another transaction
 * between the time it was loaded and the time it was updated.
 *
 * @since 1.0.0
 */
public class OptimisticLockException extends NextEntityException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public OptimisticLockException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public OptimisticLockException(String message, Throwable cause) {
        super(message, cause);
    }

}