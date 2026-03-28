package io.github.nextentity.core.exception;

import java.sql.SQLException;

/**
 * Unchecked wrapper for {@link SQLException}.
 * Provides additional context about the SQL that caused the exception.
 *
 * @since 2.0.0
 */
public class SqlException extends NextEntityException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public SqlException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public SqlException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public SqlException(Throwable cause) {
        super(cause);
    }

}