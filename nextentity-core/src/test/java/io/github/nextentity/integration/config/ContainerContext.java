package io.github.nextentity.integration.config;

/**
 * Interface for database configuration providers.
 *
 * @author HuangChengwei
 */
public interface DbConfigProvider {

    /**
     * Returns the database configuration.
     *
     * @return the DbConfig instance
     */
    DbConfig getConfig();
}
