package io.github.nextentity.integration.config;

import java.util.Collection;

/**
 * Interface for database configuration providers.
 *
 * @author HuangChengwei
 */
public interface ContainerContext {

    Collection<IntegrationTestContext> getConfigs();

    void reset(IntegrationTestContext context);

}
