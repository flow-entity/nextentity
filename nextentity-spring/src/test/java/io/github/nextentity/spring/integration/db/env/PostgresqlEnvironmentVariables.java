package io.github.nextentity.spring.integration.db.env;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/// 使用 Testcontainers 的 PostgreSQL 数据库环境变量。
///
/// @author HuangChengwei
public class PostgresqlEnvironmentVariables implements DatabaseEnvironmentVariables {

    private static final JdbcDatabaseContainer<?> CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse("postgres"))
            .withDatabaseName("nextentity")
            .withUsername("postgres")
            .withPassword("root")
            .withEnv("POSTGRES_INITDB_ARGS", "--lc-collate=C --lc-ctype=C");

    static {
        CONTAINER.start();
    }

    @Override
    public String getName() {
        return "postgresql";
    }

    @Override
    public String getJdbcUrl() {
        return CONTAINER.getJdbcUrl();
    }

    @Override
    public String getUsername() {
        return CONTAINER.getUsername();
    }

    @Override
    public String getPassword() {
        return CONTAINER.getPassword();
    }

    @Override
    public String getDriverClassName() {
        return CONTAINER.getDriverClassName();
    }

    @Override
    public String getPidNullSql() {
        return "update \"users\" set pid = null";
    }
}