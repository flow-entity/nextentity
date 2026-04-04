package io.github.nextentity.jdbc;

import io.github.nextentity.jdbc.JdbcQueryExecutor.QuerySqlBuilder;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/// 通用查询SQL构建器
///
/// 该类通过 SqlDialect 支持所有数据库方言，替代原先的三个独立 Builder。
/// 所有方言差异（分页语法、函数名映射、NOT操作处理）都由 SqlDialect 处理。
///
/// @author HuangChengwei
/// @since 2.0
public class QuerySqlBuilderImpl implements QuerySqlBuilder {

    private final SqlDialect dialect;

    public QuerySqlBuilderImpl(SqlDialect dialect) {
        this.dialect = dialect;
    }

    /// 构建查询SQL语句
    ///
    /// @param context 查询上下文
    /// @return 查询SQL语句对象
    @Override
    public QuerySqlStatement build(QueryContext context) {
        return new Builder(context, dialect).build();
    }

    /// 查询SQL构建器内部实现类
    ///
    /// 处理子查询时的状态传递（sql, args, selectIndex）。
    static class Builder extends AbstractQuerySqlBuilder {

        /// 构造函数
        ///
        /// @param context 查询上下文
        /// @param dialect SQL方言
        public Builder(QueryContext context, SqlDialect dialect) {
            super(context, dialect);
        }

        /// 构造函数（用于子查询）
        ///
        /// @param sql         SQL语句字符串构建器
        /// @param args        参数列表
        /// @param context     查询上下文
        /// @param dialect     SQL方言
        /// @param selectIndex 选择索引计数器
        /// @param subIndex    子查询索引
        public Builder(StringBuilder sql,
                       List<Object> args,
                       QueryContext context,
                       SqlDialect dialect,
                       AtomicInteger selectIndex,
                       int subIndex) {
            super(sql, args, context, dialect, selectIndex, subIndex);
        }

        /// 追加子查询结构
        ///
        /// @param subContext 子查询上下文
        @Override
        protected void appendQueryStructure(QueryContext subContext) {
            new Builder(sql, args, subContext, dialect, selectIndex, subIndex + 1).doBuilder();
        }
    }
}