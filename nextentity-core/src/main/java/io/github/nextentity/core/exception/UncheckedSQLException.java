package io.github.nextentity.core.exception;

import java.sql.SQLException;

/**
 * Unchecked wrapper for {@link SQLException}.
 * Provides additional context about the SQL that caused the exception.
 */
public class UncheckedSQLException extends RuntimeException {

    public UncheckedSQLException() {
        super();
    }

    public UncheckedSQLException(String message) {
        super(message);
    }

    public UncheckedSQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncheckedSQLException(Throwable cause) {
        super(cause);
    }

}