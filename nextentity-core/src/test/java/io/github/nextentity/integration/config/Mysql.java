package io.github.nextentity.integration.config;

import io.github.nextentity.jdbc.MySqlQuerySqlBuilder;
import io.github.nextentity.jdbc.MySqlUpdateSqlBuilder;
import io.github.nextentity.jdbc.SqlDialectSelector;
import org.jspecify.annotations.NonNull;
import org.testcontainers.mysql.MySQLContainer;

import java.util.List;

/**
 * MySQL database configuration provider using Testcontainers.
 *
 * @author HuangChengwei
 */
public class Mysql extends AbstractContainerContext implements ContainerContext {

    private static final MySQLContainer MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer("mysql:latest")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        MYSQL_CONTAINER.start();
    }

    @Override
    protected MySQLContainer getContainer() {
        return MYSQL_CONTAINER;
    }

    @Override
    protected SqlDialectSelector getSqlDialectSelector() {
        return new SqlDialectSelector()
                .setQuerySqlBuilder(new MySqlQuerySqlBuilder())
                .setUpdateSqlBuilder(new MySqlUpdateSqlBuilder());
    }

    @Override
    protected @NonNull List<String> resetDdlSql() {
        return List.of(
                "DROP TABLE IF EXISTS employee",
                "DROP TABLE IF EXISTS department",
                """
                        CREATE TABLE department (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            location VARCHAR(100),
                            budget DOUBLE,
                            active BOOLEAN,
                            created_at TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE employee (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            email VARCHAR(100),
                            salary DOUBLE,
                            active BOOLEAN,
                            status INT,
                            department_id BIGINT,
                            hire_date DATE,
                            created_at TIMESTAMP
                        )
                        """
        );
    }

    @Override
    protected @NonNull String getDialect() {
        return "mysql";
    }
}
