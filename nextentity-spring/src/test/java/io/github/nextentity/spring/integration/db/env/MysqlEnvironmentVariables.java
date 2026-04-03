package io.github.nextentity.spring.integration.db.env;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/// 使用 Testcontainers 的 MySQL 数据库环境变量。
///
/// @author HuangChengwei
public class MysqlEnvironmentVariables implements DatabaseEnvironmentVariables {

    private static final JdbcDatabaseContainer<?> CONTAINER = new MySQLContainer(
            DockerImageName.parse("mysql:latest"))
            .withDatabaseName("nextentity")
            .withUsername("root")
            .withPassword("root");

    static {
        CONTAINER.start();
    }

    @Override
    public String getName() {
        return "mysql";
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
        return "update `users` set pid = null";
    }
}