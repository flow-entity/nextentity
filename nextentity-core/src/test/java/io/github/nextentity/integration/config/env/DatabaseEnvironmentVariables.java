package io.github.nextentity.integration.config.env;

public interface DatabaseEnvironmentVariables {
    String getJdbcUrl();
    String getUsername();
    String getPassword();
    String getDriverClassName();
}
