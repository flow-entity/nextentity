package io.github.nextentity.jdbc;

/// 默认方言实现
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