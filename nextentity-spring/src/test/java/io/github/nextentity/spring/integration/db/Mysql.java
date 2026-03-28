package io.github.nextentity.spring.integration.db;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * MySQL database configuration provider using Testcontainers.
 * <p>
 * Uses MySQL 8.0 container with singleton pattern for efficient resource sharing.
 *
 * @author HuangChengwei
 */
public class Mysql extends AbstractTestcontainersDbConfigProvider {

    /**
     * Singleton MySQL container instance.
     * Started once and shared across all tests.
     */
    private static final JdbcDatabaseContainer<?> CONTAINER = new MySQLContainer<>(
            DockerImageName.parse("mysql:latest"))
            .withDatabaseName("nextentity")
            .withUsername("root")
            .withPassword("root");

    static {
        CONTAINER.start();
    }

    @Override
    protected JdbcDatabaseContainer<?> getContainer() {
        return CONTAINER;
    }

    @Override
    public String setPidNullSql() {
        return "update `users` set pid = null";
    }

    @Override
    public String name() {
        return "mysql";
    }
}
