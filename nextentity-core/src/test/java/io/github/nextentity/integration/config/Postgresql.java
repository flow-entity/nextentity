package io.github.nextentity.integration.config;

import io.github.nextentity.jdbc.PostgreSqlUpdateSqlBuilder;
import io.github.nextentity.jdbc.PostgresqlQuerySqlBuilder;
import io.github.nextentity.jdbc.SqlDialectSelector;
import org.jspecify.annotations.NonNull;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

/**
 * PostgreSQL database configuration provider using Testcontainers.
 *
 * @author HuangChengwei
 */
public class Postgresql extends AbstractContainerContext implements ContainerContext {

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
    protected PostgreSQLContainer getContainer() {
        return POSTGRESQL_CONTAINER;
    }

    @Override
    protected SqlDialectSelector getSqlDialectSelector() {
        return new SqlDialectSelector()
                .setQuerySqlBuilder(new PostgresqlQuerySqlBuilder())
                .setUpdateSqlBuilder(new PostgreSqlUpdateSqlBuilder());
    }

    @Override
    protected @NonNull List<String> resetDdlSql() {
        return List.of(
                "DROP TABLE IF EXISTS employee",
                "DROP TABLE IF EXISTS department",
                """
                        CREATE TABLE "department" (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            location VARCHAR(100),
                            budget DOUBLE PRECISION,
                            active BOOLEAN,
                            created_at TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE "employee" (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            email VARCHAR(100),
                            salary DOUBLE PRECISION,
                            active BOOLEAN,
                            status INTEGER,
                            department_id BIGINT,
                            hire_date DATE,
                            created_at TIMESTAMP
                        )
                        """
        );
    }

    @Override
    protected @NonNull String getDialect() {
        return "postgre";
    }

}
