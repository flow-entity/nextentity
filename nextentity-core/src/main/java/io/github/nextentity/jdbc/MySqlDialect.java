package io.github.nextentity.jdbc;

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

}