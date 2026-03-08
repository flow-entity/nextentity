package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ValueConvertor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcArguments extends AbstractArguments {

    private final ResultSet resultSet;

    public JdbcArguments(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public Object get(int index, ValueConvertor<?, ?> convertor) {
        try {
            return JdbcUtil.getValue(resultSet, 1 + index, convertor);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}