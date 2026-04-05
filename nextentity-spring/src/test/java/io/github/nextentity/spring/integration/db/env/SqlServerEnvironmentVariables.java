package io.github.nextentity.spring.integration.db.env;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.mssqlserver.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

/// 使用 Testcontainers 的 SQL Server 数据库环境变量。
///
/// @author HuangChengwei
public class SqlServerEnvironmentVariables implements DatabaseEnvironmentVariables {

    private static final JdbcDatabaseContainer<?> CONTAINER = new MSSQLServerContainer(
            DockerImageName.parse("mcr.microsoft.com/mssql/server:latest"))
            .acceptLicense();

    static {
        CONTAINER.start();
    }

    @Override
    public String getName() {
        return "sqlserver";
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
        return "UPDATE [users] SET pid = NULL";
    }
}