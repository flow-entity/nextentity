package io.github.nextentity.jdbc;

import java.util.List;

/// PostgreSQL SQL 方言实现
///
/// PostgreSQL 特性：
/// - 使用双引号 (") 作为标识符引用字符
/// - 分页语法：LIMIT limit OFFSET offset
/// - 日期类型占位符使用 ::timestamp 类型转换
///
/// @author HuangChengwei
/// @since 2.0
public class PostgresqlDialect implements SqlDialect {

    @Override
    public String leftQuotedIdentifier() {
        return "\"";
    }

    @Override
    public String rightQuotedIdentifier() {
        return "\"";
    }

    @Override
    public void appendLimitOffset(StringBuilder sql, List<Object> args, int offset, int limit) {
        // PostgreSQL style: LIMIT limit OFFSET offset
        if (offset > 0 || limit > 0) {
            sql.append(" limit ? offset ?");
            args.add(limit < 0 ? Long.MAX_VALUE : limit);
            args.add(Math.max(offset, 0));
        }
    }

    @Override
    public String typedPlaceholder(Class<?> type) {
        // PostgreSQL uses ::timestamp cast for date types
        if (java.util.Date.class.isAssignableFrom(type) || java.sql.Timestamp.class.isAssignableFrom(type)) {
            return "?::timestamp";
        }
        return "?";
    }

    @Override
    public UpdateJoinStyle getUpdateJoinStyle() {
        // PostgreSQL: UPDATE table alias SET ... FROM other JOIN ... WHERE ...
        return UpdateJoinStyle.FROM_CLAUSE_WITH_JOIN;
    }
}