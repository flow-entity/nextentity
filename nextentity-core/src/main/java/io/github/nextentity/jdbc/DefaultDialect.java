package io.github.nextentity.jdbc;

/// 默认方言实现
///
/// 使用标准 SQL 语法，适用于未知数据库类型时的默认选择。
/// 当无法识别具体数据库类型时，此方言提供最通用的 SQL 兼容性。
///
/// 特性：
/// - 使用标准 SQL 双引号作为标识符引用符
/// - 采用 MySQL 风格的分页语法（继承自 SqlDialect 默认实现）
/// - 函数名不做映射（继承自 SqlDialect 默认实现）
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

}