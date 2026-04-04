package io.github.nextentity.jdbc;

/// 统一的 SQL 构建器
///
/// 该类合并了查询和更新 SQL 构建功能，同时实现 QuerySqlBuilder 和 JdbcUpdateSqlBuilder 接口。
/// 通过 SqlDialect 支持所有数据库方言（MySQL、PostgreSQL、SQL Server），方言差异由 SqlDialect 处理。
///
/// @author HuangChengwei
/// @since 2.0
public interface SqlBuilder extends QuerySqlBuilder, JdbcUpdateSqlBuilder {

    static SqlBuilder of(SqlDialect sqlDialect) {
        return new DefaultSqlBuilder(sqlDialect);
    }
}