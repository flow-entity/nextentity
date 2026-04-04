package io.github.nextentity.jdbc;

import java.util.List;

/// SQL Server SQL 方言实现
///
/// SQL Server 特性：
/// - 使用方括号 ([]) 作为标识符引用字符
/// - 分页语法：OFFSET offset ROWS FETCH FIRST limit ROWS ONLY
/// - 分页需要 ORDER BY 子句
/// - LENGTH 函数使用 'len' 而非 'length'
/// - NOT path 转换为 path = false
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
        if (offset > 0 || limit >= 0) {
            sql.append(" offset ? rows");
            args.add(Math.max(offset, 0));
            if (limit >= 0) {
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
}