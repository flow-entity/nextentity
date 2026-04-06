package io.github.nextentity.jdbc;

import java.util.List;

/// MySQL SQL 方言实现
///
/// MySQL 特性：
/// - 使用反引号 (`) 作为标识符引用字符
/// - 分页语法：LIMIT offset,limit 或 LIMIT limit
///
/// @author HuangChengwei
/// @since 2.0
public class MySqlDialect implements SqlDialect {

    @Override
    public String leftQuotedIdentifier() {
        return "`";
    }

    @Override
    public String rightQuotedIdentifier() {
        return "`";
    }

    @Override
    public void appendLimitOffset(StringBuilder sql, List<Object> args, int offset, int limit) {
        if (offset > 0) {
            sql.append(" limit ").append(offset).append(",").append(limit < 0 ? Long.MAX_VALUE : limit);
        } else if (limit >= 0) {
            sql.append(" limit ").append(limit);
        }
    }

}