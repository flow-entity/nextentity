package io.github.nextentity.spring.integration.db;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * SQL Server database configuration provider using Testcontainers.
 * <p>
 * Uses SQL Server 2022 container with singleton pattern for efficient resource sharing.
 * <p>
 * Note: SQL Server container is heavy (~1.5GB image, slower startup).
 * Consider running separately when needed by uncommenting in DbConfigs.
 *
 * @author HuangChengwei
 * @since 2024-04-09 15:00
 */
public class SqlServer extends AbstractTestcontainersDbConfigProvider {

    /**
     * Singleton SQL Server container instance.
     * Started once and shared across all tests.
     */
    @SuppressWarnings("resource")
    private static final JdbcDatabaseContainer<?> CONTAINER = new MSSQLServerContainer<>(
            DockerImageName.parse("mcr.microsoft.com/mssql/server"))
            .acceptLicense()
            .withDatabaseName("nextentity")
            .withUsername("sa")
            .withPassword("Root_Passw0rd!");

    static {
        CONTAINER.start();
    }

    @Override
    protected JdbcDatabaseContainer<?> getContainer() {
        return CONTAINER;
    }

    @Override
    public String setPidNullSql() {
        return "update [user] set pid = null";
    }

    @Override
    public String name() {
        return "sqlserver";
    }
}
