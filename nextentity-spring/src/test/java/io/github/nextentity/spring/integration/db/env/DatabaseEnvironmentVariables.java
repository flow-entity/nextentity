package io.github.nextentity.spring.integration.db.env;

import java.util.List;

/**
 * Database environment variables interface.
 * Provides database connection configuration for integration tests.
 *
 * @author HuangChengwei
 */
public interface DatabaseEnvironmentVariables {

    List<DatabaseEnvironmentVariables> DBS = List.of(
            new MysqlEnvironmentVariables(),
            new PostgresqlEnvironmentVariables()
    );

    /**
     * Returns the database name (e.g., "mysql", "postgresql").
     */
    String getName();

    /**
     * Returns the JDBC connection URL.
     */
    String getJdbcUrl();

    /**
     * Returns the database username.
     */
    String getUsername();

    /**
     * Returns the database password.
     */
    String getPassword();

    /**
     * Returns the JDBC driver class name.
     */
    String getDriverClassName();

    /**
     * Returns the SQL statement to set parent ID to null for data reset.
     */
    String getPidNullSql();
}