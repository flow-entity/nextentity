package io.github.nextentity.integration.config.env;

import java.util.List;

public interface DatabaseEnvironmentVariables {
    String name();

    String getJdbcUrl();

    String getUsername();

    String getPassword();

    String getDriverClassName();

    List<String> ddl();
}
