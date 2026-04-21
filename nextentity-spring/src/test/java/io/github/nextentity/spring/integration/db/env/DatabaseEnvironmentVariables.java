package io.github.nextentity.spring.integration.db.env;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/// 数据库环境变量接口。
/// 为集成测试提供数据库连接配置。
///
/// @author HuangChengwei
public interface DatabaseEnvironmentVariables {

    Map<String, Supplier<DatabaseEnvironmentVariables>> DBS = Map.of(
            "mysql", MysqlEnvironmentVariables::new,
            "postgresql", PostgresqlEnvironmentVariables::new,
            "sqlserver", SqlServerEnvironmentVariables::new,
            "h2", H2EnvironmentVariables::new
    );

    static List<DatabaseEnvironmentVariables> dbs(String... sources) {
        if(sources==null||sources.length==0){
            return DBS.values().stream().map(Supplier::get).toList();
        }
        return Arrays.stream(sources)
                .map(DBS::get)
                .filter(Objects::nonNull)
                .map(Supplier::get)
                .toList();

    }

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