package io.github.nextentity.jdbc;

/// SQL 构建器接口
///
/// 定义查询和更新 SQL 语句的构建方法，支持批量操作和条件操作。
/// 实现类通过 SqlDialect 处理数据库特定的语法差异。
///
/// @author HuangChengwei
/// @since 2.0
public interface SqlBuilder extends QuerySqlBuilder, JdbcUpdateSqlBuilder {

    static SqlBuilder of(SqlDialect sqlDialect) {
        return new DefaultSqlBuilder(sqlDialect);
    }
}