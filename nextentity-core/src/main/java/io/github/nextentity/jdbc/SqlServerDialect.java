package io.github.nextentity.jdbc;

import jakarta.persistence.LockModeType;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/// SQL Server SQL 方言实现
///
/// SQL Server 特性：
/// - 使用方括号 ([]) 作为标识符引用字符
/// - 分页语法：OFFSET offset ROWS FETCH FIRST limit ROWS ONLY
/// - 分页需要 ORDER BY 子句
/// - LENGTH 函数使用 'len' 而非 'length'
/// - NOT path 转换为 path = false
/// - 锁模式使用 WITH (UPDLOCK) 提示，不支持 FOR SHARE/FOR UPDATE
/// - UPDATE JOIN 语法：UPDATE alias SET ... FROM table alias JOIN ... WHERE ...
/// - DELETE JOIN 语法：DELETE alias FROM table alias JOIN ... WHERE ...
///
/// @author HuangChengwei
/// @since 2.0
public class SqlServerDialect implements SqlDialect {

    static {
        SqlDialect.register(new SqlServerDialect());
    }

    @Override
    public int priority() {
        return 3000;
    }

    @Override
    public boolean matches(DatabaseMetaData metaData) throws SQLException {
        String driverName = metaData.getDriverName().toLowerCase();
        return driverName.contains("mssql") || driverName.contains("sql server");
    }

    @Override
    public String leftQuotedIdentifier() {
        return "[";
    }

    @Override
    public String rightQuotedIdentifier() {
        return "]";
    }

    @Override
    public void appendLimitOffset(StringBuilder sql, List<Object> args, int offset, int limit) {
        // SQL Server style: OFFSET offset ROWS FETCH FIRST limit ROWS ONLY
        // Note: SQL Server requires limit > 0 for FETCH clause
        if (offset > 0 || limit > 0) {
            sql.append(" offset ? rows");
            args.add(Math.max(offset, 0));
            if (limit > 0) {
                sql.append(" fetch first ? rows only");
                args.add(limit);
            }
        }
    }

    @Override
    public String functionName(String name) {
        // SQL Server uses "len" instead of "length"
        return "length".equals(name) ? "len" : name;
    }

    @Override
    public boolean requiresOrderByForPagination() {
        return true;
    }

    @Override
    public boolean shouldConvertNotToEqFalse() {
        return true;
    }

    @Override
    public boolean supportsForUpdateSyntax() {
        return false;
    }

    @Override
    public boolean requiresAliasForAggregateColumns() {
        return true;
    }

    @Override
    public boolean supportsBatchGeneratedKeys() {
        // SQL Server JDBC driver has issues with batch getGeneratedKeys
        // Need to execute inserts one by one to reliably get generated keys
        return false;
    }

    @Override
    public void appendLockMode(StringBuilder sql, LockModeType lockModeType) {
        // SQL Server uses table hints instead of FOR SHARE/FOR UPDATE
        // Note: These hints are placed after the table name in FROM clause,
        // but for simplicity we append at the end (works for simple queries)
        if (lockModeType == LockModeType.PESSIMISTIC_READ) {
            // PESSIMISTIC_READ: Use ROWLOCK hint (shared lock)
            sql.append(" with (rowlock)");
        } else if (lockModeType == LockModeType.PESSIMISTIC_WRITE) {
            // PESSIMISTIC_WRITE: Use UPDLOCK + ROWLOCK (update lock)
            sql.append(" with (updlock, rowlock)");
        } else if (lockModeType == LockModeType.PESSIMISTIC_FORCE_INCREMENT) {
            // PESSIMISTIC_FORCE_INCREMENT: Use UPDLOCK + ROWLOCK + NOWAIT
            sql.append(" with (updlock, rowlock, nowait)");
        }
    }

    // ========== UPDATE/DELETE JOIN 方法实现 ==========

    @Override
    public boolean supportsUpdateAliasOnlySyntax() {
        return true;  // SQL Server 支持 UPDATE ALIAS ONLY 语法
    }

    @Override
    public boolean supportsUpdateFromSyntax() {
        return false;  // SQL Server 不支持标准 UPDATE FROM 语法（有自己的风格）
    }

    @Override
    public boolean supportsDeleteUsingSyntax() {
        return false;  // SQL Server 不支持 DELETE USING 语法
    }

    @Override
    public void appendUpdateClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // SQL Server: UPDATE alias SET ... (有 JOIN)
        //             UPDATE alias SET ... (无 JOIN，但需要 FROM 子句来支持别名)
        sql.append("update ").append(alias);
    }

    @Override
    public void appendUpdateFromClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // SQL Server: FROM table alias [JOIN ...] (总是需要 FROM 子句来支持别名)
        sql.append(" from ").append(table).append(" ").append(alias);
        // JOIN 子句由 Builder 追加（如有）
    }

    @Override
    public void appendDeleteClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // SQL Server: DELETE alias FROM table alias ... (有 JOIN)
        //             DELETE alias FROM table alias ... (无 JOIN，也需要 FROM)
        sql.append("delete ").append(alias);
    }

    @Override
    public void appendDeleteFromClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // SQL Server: FROM table alias JOIN ... (总是需要 FROM)
        sql.append(" from ").append(table).append(" ").append(alias);
        // JOIN 子句由 Builder 追加（如有）
    }

    @Override
    public void appendWhereClause(StringBuilder sql, WhereClauseContext context) {
        // SQL Server: JOIN 条件已在 ON 子句中，直接追加用户 WHERE 条件
        if (!context.isNullOrTrue(context.whereCondition())) {
            sql.append(" where ");
            context.appendPredicate(context.whereCondition());
        }
    }

}