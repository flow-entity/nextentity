package io.github.nextentity.jdbc;

import java.util.List;

/// SQL statement for conditional update operations.
///
/// @author HuangChengwei
/// @since 2.1
public class UpdateSqlStatement implements SqlStatement {

    private final String sql;
    private final List<?> parameters;

    public UpdateSqlStatement(String sql, List<?> parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    @Override
    public String sql() {
        return sql;
    }

    @Override
    public Iterable<?> parameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return sql;
    }
}