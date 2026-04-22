package io.github.nextentity.integration.config.env;

import org.testcontainers.containers.JdbcDatabaseContainer;

public abstract class DbContainerEnvironmentVariables implements DatabaseEnvironmentVariables {

    protected abstract JdbcDatabaseContainer<?> getContainer();

    @Override
    public String getJdbcUrl() {
        return getContainer().getJdbcUrl();
    }

    @Override
    public String getUsername() {
        return getContainer().getUsername();
    }

    @Override
    public String getPassword() {
        return getContainer().getPassword();
    }

    @Override
    public String getDriverClassName() {
        return getContainer().getDriverClassName();
    }
}
