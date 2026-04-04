package io.github.nextentity.jdbc;

import java.util.List;

/// 默认方言实现
///
/// 使用标准 SQL 语法，适用于未知数据库类型时的默认选择。
/// 当无法识别具体数据库类型时，此方言提供最通用的 SQL 兼容性。
///
/// 特性：
/// - 使用标准 SQL 双引号作为标识符引用符
/// - 采用 MySQL 风格的分页语法（最广泛支持）
/// - 函数名不做映射（使用标准函数名）
///
/// @author HuangChengwei
/// @since 2.0
public class DefaultDialect implements SqlDialect {

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
        // MySQL style: LIMIT offset,limit (most widely supported)
        if (offset > 0) {
            sql.append(" limit ?,?");
            args.add(offset);
            args.add(limit < 0 ? Long.MAX_VALUE : limit);
        } else if (limit >= 0) {
            sql.append(" limit ?");
            args.add(limit);
        }
    }

}