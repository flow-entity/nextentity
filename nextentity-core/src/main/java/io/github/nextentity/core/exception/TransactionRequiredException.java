package io.github.nextentity.core.exception;

/**
 * Exception thrown when a transaction is required but not active.
 * This typically occurs when database operations are performed outside
 * of a transactional context.
 */
public class TransactionRequiredException extends RuntimeException {

    public TransactionRequiredException() {
        super("Transaction is required but not active. Please ensure the operation is performed within a transactional context.");
    }

    public TransactionRequiredException(String message) {
        super(message);
    }

    public TransactionRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionRequiredException(Throwable cause) {
        super(cause);
    }
}
