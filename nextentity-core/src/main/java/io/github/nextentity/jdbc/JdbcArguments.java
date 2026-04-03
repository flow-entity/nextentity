package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ValueConverter;

import java.sql.ResultSet;
import java.sql.SQLException;

///
/// JDBC参数实现类
///
/// 该类用于从JDBC结果集中获取参数值，它继承自AbstractArguments抽象类，
/// 提供了基于JDBC ResultSet的具体实现。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class JdbcArguments extends AbstractArguments {

    private final ResultSet resultSet;

    /// 构造JDBC参数对象
    ///
    /// @param resultSet JDBC结果集
    public JdbcArguments(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    /// 获取指定索引位置的参数值
    ///
    /// @param index 参数索引
    /// @param convertor 值转换器
    /// @return 参数值
    @Override
    public Object get(int index, ValueConverter<?, ?> convertor) {
        try {
            return JdbcUtil.getValue(resultSet, 1 + index, convertor);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}