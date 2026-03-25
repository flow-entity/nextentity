package io.github.nextentity.integration.config.env;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

public class PostgresqlEnvironmentVariables extends DbContainerEnvironmentVariables {

    private static final PostgreSQLContainer POSTGRESQL_CONTAINER;

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer("postgres:latest")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withEnv("POSTGRES_INITDB_ARGS", "--lc-collate=C --lc-ctype=C");
        POSTGRESQL_CONTAINER.start();
    }

    @Override
    protected JdbcDatabaseContainer<?> getContainer() {
        return POSTGRESQL_CONTAINER;
    }

}
