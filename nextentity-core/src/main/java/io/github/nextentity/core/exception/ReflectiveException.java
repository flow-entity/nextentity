package io.github.nextentity.core.exception;

/**
 * Exception thrown when reflection operations fail.
 * This includes bean property access, method invocation, and other reflective operations.
 *
 * @since 2.0.0
 */
public class ReflectiveException extends NextEntityException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ReflectiveException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public ReflectiveException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public ReflectiveException(Throwable cause) {
        super(cause);
    }

}