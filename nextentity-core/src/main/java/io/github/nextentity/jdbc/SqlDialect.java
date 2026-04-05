package io.github.nextentity.jdbc;

import jakarta.persistence.LockModeType;
import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/// SQL 方言接口
///
/// 定义数据库特定的 SQL 语法差异，包括标识符引用、分页策略、函数名映射等。
/// 新增方言只需实现此接口，无需修改 Builder 类。
///
/// @author HuangChengwei
/// @since 2.0.0
public interface SqlDialect {

    /// 返回标识符的左引号字符
    ///
    /// @return 左引号字符（例如 MySQL 使用 "`"，标准 SQL 使用 "\""）
    String leftQuotedIdentifier();

    /// 返回标识符的右引号字符
    ///
    /// @return 右引号字符（例如 MySQL 使用 "`"，标准 SQL 使用 "\""）
    String rightQuotedIdentifier();

    /// 添加 LIMIT 和 OFFSET 子句到 SQL 语句
    ///
    /// 不同数据库有不同的分页语法：
    /// - MySQL: LIMIT offset,limit 或 LIMIT limit
    /// - PostgreSQL: LIMIT limit OFFSET offset
    /// - SQL Server: OFFSET offset ROWS FETCH FIRST limit ROWS ONLY
    ///
    /// @param sql    要追加的 SQL 构建器
    /// @param args   要添加值的参数列表
    /// @param offset 偏移量（无偏移时为 0）
    /// @param limit  限制数（无限制时为 -1）
    void appendLimitOffset(StringBuilder sql, List<Object> args, int offset, int limit);

    /// 将函数名映射到数据库特定的名称
    ///
    /// 有些数据库使用不同的函数名：
    /// - SQL Server 使用 "len" 而不是 "length"
    ///
    /// @param name 标准函数名
    /// @return 数据库特定的函数名
    default String functionName(String name) {
        return name;
    }

    /// 返回数据库是否需要 ORDER BY 子句才能进行分页
    ///
    /// SQL Server 在 OFFSET/FETCH 子句之前需要 ORDER BY。
    ///
    /// @return 如果分页需要 ORDER BY 则返回 true
    default boolean requiresOrderByForPagination() {
        return false;
    }

    /// 返回是否应该将 NOT path 转换为 path = false
    ///
    /// SQL Server 对布尔表达式的 NOT 操作有特殊处理。
    ///
    /// @return 如果应将 NOT path 转换为 path = false 则返回 true
    default boolean shouldConvertNotToEqFalse() {
        return false;
    }

    /// 返回给定属性类型的类型化占位符
    ///
    /// PostgreSQL 对日期类型使用 "::timestamp" 类型转换。
    ///
    /// @param type 属性类型
    /// @return 类型化占位符字符串
    default String typedPlaceholder(Class<?> type) {
        return "?";
    }

    /// 返回数据库是否支持 FOR SHARE/FOR UPDATE 锁模式语法
    ///
    /// SQL Server 不支持此语法，需要使用 WITH (UPDLOCK) 提示。
    ///
    /// @return 如果支持 FOR SHARE/FOR UPDATE 语法则返回 true
    default boolean supportsForUpdateSyntax() {
        return true;
    }

    /// 返回是否需要为子查询中的聚合列添加别名
    ///
    /// SQL Server 要求子查询中的所有列必须有名称，聚合函数列如 count(id) 需要别名。
    ///
    /// @return 如果需要为聚合列添加别名则返回 true
    default boolean requiresAliasForAggregateColumns() {
        return false;
    }

    /// 添加锁模式子句到 SQL 语句
    ///
    /// 不同数据库有不同的锁模式语法：
    /// - PostgreSQL/MySQL: FOR SHARE, FOR UPDATE, FOR UPDATE NOWAIT
    /// - SQL Server: WITH (ROWLOCK), WITH (UPDLOCK, ROWLOCK), WITH (UPDLOCK, ROWLOCK, NOWAIT)
    ///
    /// @param sql         要追加的 SQL 构建器
    /// @param lockModeType JPA 锁模式类型
    default void appendLockMode(StringBuilder sql, LockModeType lockModeType) {
        if (lockModeType == LockModeType.PESSIMISTIC_READ) {
            sql.append(" for share");
        } else if (lockModeType == LockModeType.PESSIMISTIC_WRITE) {
            sql.append(" for update");
        } else if (lockModeType == LockModeType.PESSIMISTIC_FORCE_INCREMENT) {
            sql.append(" for update nowait");
        }
    }

    /// 默认 SQL 方言实例
    SqlDialect DEFAULT = new DefaultDialect();

    /// MySQL SQL 方言实例
    SqlDialect MYSQL = new MySqlDialect();

    /// PostgreSQL SQL 方言实例
    SqlDialect POSTGRESQL = new PostgresqlDialect();

    /// SQL Server SQL 方言实例
    SqlDialect SQL_SERVER = new SqlServerDialect();

    /// 根据数据源自动检测 SQL 方言
    ///
    /// 通过读取数据库元数据中的驱动名称来判断数据库类型：
    /// - MySQL/MariaDB → MYSQL
    /// - PostgreSQL → POSTGRESQL
    /// - SQL Server → SQL_SERVER
    /// - 其他 → MYSQL（默认）
    ///
    /// @param dataSource 数据源
    /// @return 对应的 SQL 方言实例
    /// @throws SQLException 如果获取数据库连接失败
    static SqlDialect detectFromDataSource(DataSource dataSource) throws SQLException {
        try (var connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String driverName = metaData.getDriverName().toLowerCase();

            if (driverName.contains("mysql") || driverName.contains("maria")) {
                return MYSQL;
            } else if (driverName.contains("mssql") || driverName.contains("sql server")) {
                return SQL_SERVER;
            } else if (driverName.contains("postgresql")) {
                return POSTGRESQL;
            } else {
                return DEFAULT;
            }
        }
    }
}