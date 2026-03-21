package io.github.nextentity.integration.config;

import java.util.Collection;

/**
 * Interface for database configuration providers.
 *
 * @author HuangChengwei
 */
public interface ContainerContext {

    Collection<DbConfig> getConfigs();

    void reset(DbConfig config);

}
