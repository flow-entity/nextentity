package io.github.nextentity.jdbc;

/// SQL 构建器接口
///
/// 定义查询和更新 SQL 语句的构建方法，支持批量操作和条件操作。
/// 实现类通过 SqlDialect 处理数据库特定的语法差异。
///
/// @author HuangChengwei
/// @since 2.0
public interface SqlBuilder extends QuerySqlBuilder, JdbcUpdateSqlBuilder {

    /// 创建 SQL 构建器（使用默认配置）
    ///
    /// @param sqlDialect SQL 方言
    /// @return SQL 构建器实例
    static SqlBuilder of(SqlDialect sqlDialect) {
        return new DefaultSqlBuilder(sqlDialect, JdbcConfig.DEFAULT);
    }

    /// 创建 SQL 构建器
    ///
    /// @param sqlDialect SQL 方言
    /// @param config     JDBC 配置
    /// @return SQL 构建器实例
    static SqlBuilder of(SqlDialect sqlDialect, JdbcConfig config) {
        return new DefaultSqlBuilder(sqlDialect, config);
    }
}