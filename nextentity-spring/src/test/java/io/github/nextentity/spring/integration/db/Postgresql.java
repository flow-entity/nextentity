package io.github.nextentity.spring.integration.db;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * PostgreSQL database configuration provider using Testcontainers.
 * <p>
 * Uses PostgreSQL 15 Alpine container with singleton pattern for efficient resource sharing.
 *
 * @author HuangChengwei
 */
public class Postgresql extends AbstractTestcontainersDbConfigProvider {

    /**
     * Singleton PostgreSQL container instance.
     * Started once and shared across all tests.
     */
    private static final JdbcDatabaseContainer<?> CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres"))
            .withDatabaseName("nextentity")
            .withUsername("postgres")
            .withPassword("root")
            .withEnv("POSTGRES_INITDB_ARGS", "--lc-collate=C --lc-ctype=C");

    static {
        CONTAINER.start();
    }

    @Override
    protected JdbcDatabaseContainer<?> getContainer() {
        return CONTAINER;
    }

    @Override
    public String setPidNullSql() {
        return "update \"user\" set pid = null";
    }

    @Override
    public String name() {
        return "postgresql";
    }
}
