package io.github.nextentity.core.exception;

/**
 * Exception thrown when a repository configuration error occurs.
 * This typically happens when entity type cannot be resolved from generic parameters
 * or when the repository is not properly configured.
 */
public class RepositoryConfigurationException extends RuntimeException {

    public RepositoryConfigurationException(String message) {
        super(message);
    }

    public RepositoryConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryConfigurationException(Throwable cause) {
        super(cause);
    }
}
