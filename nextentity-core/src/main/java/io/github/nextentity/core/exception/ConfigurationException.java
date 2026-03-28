package io.github.nextentity.core.exception;

/**
 * Exception thrown when a configuration error occurs.
 * This typically happens when entity type cannot be resolved from generic parameters
 * or when the repository is not properly configured.
 *
 * @since 2.0.0
 */
public class ConfigurationException extends NextEntityException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }

}