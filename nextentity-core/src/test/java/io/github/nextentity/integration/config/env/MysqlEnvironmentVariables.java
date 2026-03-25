package io.github.nextentity.integration.config.env;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.mysql.MySQLContainer;

public class MysqlEnvironmentVariables extends DbContainerEnvironmentVariables {

    private static final MySQLContainer MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer("mysql:latest")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        MYSQL_CONTAINER.start();
    }

    @Override
    protected JdbcDatabaseContainer<?> getContainer() {
        return MYSQL_CONTAINER;
    }

}
