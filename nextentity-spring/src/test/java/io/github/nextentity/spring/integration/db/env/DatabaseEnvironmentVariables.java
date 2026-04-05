package io.github.nextentity.spring.integration.db.env;

import java.util.List;

/// 数据库环境变量接口。
/// 为集成测试提供数据库连接配置。
///
/// @author HuangChengwei
public interface DatabaseEnvironmentVariables {

    List<DatabaseEnvironmentVariables> DBS = List.of(
            new MysqlEnvironmentVariables(),
            new PostgresqlEnvironmentVariables(),
            new SqlServerEnvironmentVariables()
    );

    /// 返回数据库名称（例如 "mysql", "postgresql"）。
    String getName();

    /// 返回 JDBC 连接 URL。
    String getJdbcUrl();

    /// 返回数据库用户名。
    String getUsername();

    /// 返回数据库密码。
    String getPassword();

    /// 返回 JDBC 驱动类名。
    String getDriverClassName();

    /// 返回用于数据重置的将父 ID 设置为 NULL 的 SQL 语句。
    String getPidNullSql();
}