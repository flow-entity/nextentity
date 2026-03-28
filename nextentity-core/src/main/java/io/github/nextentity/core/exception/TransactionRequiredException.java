package io.github.nextentity.core.exception;

/**
 * Exception thrown when a transaction is required but not active.
 * This typically occurs when database operations are performed outside
 * of a transactional context.
 *
 * @since 1.0.0
 */
public class TransactionRequiredException extends NextEntityException {

    /**
     * Constructs a new exception with a default message.
     */
    public TransactionRequiredException() {
        super("Transaction is required but not active. Please ensure the operation is performed within a transactional context.");
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public TransactionRequiredException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public TransactionRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public TransactionRequiredException(Throwable cause) {
        super(cause);
    }

}