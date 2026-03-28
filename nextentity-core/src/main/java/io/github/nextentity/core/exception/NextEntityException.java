package io.github.nextentity.core.exception;

/**
 * Base exception class for all NextEntity framework exceptions.
 * All framework-specific exceptions should extend this class.
 *
 * @since 2.0.0
 */
public class NextEntityException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public NextEntityException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public NextEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public NextEntityException(Throwable cause) {
        super(cause);
    }

}