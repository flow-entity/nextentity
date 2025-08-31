package io.github.nextentity.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcArguments extends AbstractArguments {

    private final ResultSet resultSet;

    public JdbcArguments(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public Object get(int index, Class<?> type) {
        try {
            return JdbcUtil.getValue(resultSet, 1 + index, type);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}