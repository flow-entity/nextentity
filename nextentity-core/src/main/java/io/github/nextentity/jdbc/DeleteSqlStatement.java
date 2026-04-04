package io.github.nextentity.jdbc;

import java.util.List;

/// SQL statement for conditional delete operations.
///
/// @author HuangChengwei
/// @since 2.0.0
public class DeleteSqlStatement implements SqlStatement {

    private final String sql;
    private final List<?> parameters;

    public DeleteSqlStatement(String sql, List<?> parameters) {
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