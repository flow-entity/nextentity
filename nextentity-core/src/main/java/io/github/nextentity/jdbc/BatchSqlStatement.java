package io.github.nextentity.jdbc;

///
/// 批量SQL语句类
///
/// 该类封装了用于批量执行的SQL语句及其参数，实现了SqlStatement接口，
/// 主要用于处理批量更新、插入或删除操作。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class BatchSqlStatement implements SqlStatement {
    private final String sql;
    private final Iterable<? extends Iterable<?>> parameters;

    /// 构造批量SQL语句对象
    ///
    /// @param sql SQL语句字符串
    /// @param parameters 批量操作的参数集合，外层集合代表不同的批次，内层集合代表每批次的参数
    public BatchSqlStatement(String sql, Iterable<? extends Iterable<?>> parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    /// 获取SQL语句
    ///
    /// @return SQL语句字符串
    @Override
    public String sql() {
        return sql;
    }

    /// 获取参数集合
    ///
    /// @return 参数集合，外层集合代表不同的批次，内层集合代表每批次的参数
    @Override
    public Iterable<? extends Iterable<?>> parameters() {
        return parameters;
    }
}
