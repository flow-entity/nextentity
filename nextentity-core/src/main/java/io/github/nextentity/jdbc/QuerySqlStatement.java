package io.github.nextentity.jdbc;

import java.util.List;

///
/// 查询SQL语句类
///
/// 该类封装了SQL查询语句及其参数，提供了统一的方式来管理和执行SQL语句。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class QuerySqlStatement implements SqlStatement {

    private final String sql;

    private final List<?> parameters;

    /// 构造查询SQL语句对象
    ///
    /// @param sql SQL语句字符串
    /// @param parameters SQL语句中使用的参数列表
    public QuerySqlStatement(String sql, List<?> parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return sql;
    }

    /// 获取SQL语句
    ///
    /// @return SQL语句字符串
    public String sql() {
        return sql;
    }

    /// 获取参数列表
    ///
    /// @return SQL语句中使用的参数列表
    public Iterable<?> parameters() {
        return parameters;
    }
}
