package io.github.nextentity.jdbc;

import jakarta.persistence.LockModeType;
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
///
/// @author HuangChengwei
/// @since 2.0
public class SqlServerDialect implements SqlDialect {

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
}