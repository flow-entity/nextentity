package io.github.nextentity.jdbc;

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
/// @since 2.1
public interface SqlDialect {

    /// Returns the left quote character for identifiers.
    ///
    /// @return Left quote character (e.g., "`" for MySQL, "\"" for standard SQL)
    default String leftQuotedIdentifier() {
        return "";
    }

    /// Returns the right quote character for identifiers.
    ///
    /// @return Right quote character (e.g., "`" for MySQL, "\"" for standard SQL)
    default String rightQuotedIdentifier() {
        return "";
    }

    /// Appends LIMIT and OFFSET clause to the SQL statement.
    ///
    /// Different databases have different pagination syntax:
    /// - MySQL: LIMIT offset,limit or LIMIT limit
    /// - PostgreSQL: LIMIT limit OFFSET offset
    /// - SQL Server: OFFSET offset ROWS FETCH FIRST limit ROWS ONLY
    ///
    /// @param sql    the SQL builder to append to
    /// @param args   the parameter list to add values to
    /// @param offset the offset value (0 if no offset)
    /// @param limit  the limit value (-1 if no limit)
    default void appendLimitOffset(StringBuilder sql, List<Object> args, int offset, int limit) {
        // MySQL style: LIMIT offset,limit
        if (offset > 0) {
            sql.append(" limit ?,?");
            args.add(offset);
            args.add(limit < 0 ? Long.MAX_VALUE : limit);
        } else if (limit >= 0) {
            sql.append(" limit ?");
            args.add(limit);
        }
    }

    /// Maps a function name to the database-specific name.
    ///
    /// Some databases use different function names:
    /// - SQL Server uses "len" instead of "length"
    ///
    /// @param name the standard function name
    /// @return the database-specific function name
    default String functionName(String name) {
        return name;
    }

    /// Returns whether the database requires ORDER BY clause for pagination.
    ///
    /// SQL Server requires ORDER BY before OFFSET/FETCH clauses.
    ///
    /// @return true if ORDER BY is required for pagination
    default boolean requiresOrderByForPagination() {
        return false;
    }

    /// Returns whether NOT path should be converted to path = false.
    ///
    /// SQL Server has special handling for NOT operations on boolean expressions.
    ///
    /// @return true if NOT path should be converted to path = false
    default boolean shouldConvertNotToEqFalse() {
        return false;
    }

    /// Returns a typed placeholder for the given attribute type.
    ///
    /// PostgreSQL uses "::timestamp" cast for date types.
    ///
    /// @param type the attribute type
    /// @return the typed placeholder string
    default String typedPlaceholder(Class<?> type) {
        return "?";
    }

    /// Default SQL dialect instance.
    SqlDialect DEFAULT = new DefaultDialect();

    /// MySQL SQL dialect instance.
    SqlDialect MYSQL = new MySqlDialect();

    /// PostgreSQL SQL dialect instance.
    SqlDialect POSTGRESQL = new PostgresqlDialect();

    /// SQL Server SQL dialect instance.
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