package io.github.nextentity.jdbc;

import io.github.nextentity.core.converter.TypeConverter;
import io.github.nextentity.core.reflect.TypedArguments;
import io.github.nextentity.core.reflect.schema.InstanceFactory;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.List;

public class JdbcArguments extends TypedArguments {

    private final ResultSet resultSet;

    public JdbcArguments(ResultSet resultSet, List<? extends InstanceFactory.PrimitiveFactory> types, TypeConverter typeConverter) {
        super(types, typeConverter);
        this.resultSet = resultSet;
    }


    @SneakyThrows
    @Override
    protected Object getValue(int index, Class<?> type) {
        return JdbcUtil.getValue(resultSet, ++index, type);
    }


}