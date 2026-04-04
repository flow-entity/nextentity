package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.jdbc.JdbcQueryExecutor.QuerySqlBuilder;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

///
/// SQL方言选择器
///
/// 该类根据数据源自动选择合适的SQL方言，支持MySQL、SQL Server、PostgreSQL等数据库。
/// 使用统一的 QuerySqlBuilderImpl 和 AbstractUpdateSqlBuilder，通过 SqlDialect 处理方言差异。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class SqlDialectSelector implements QuerySqlBuilder, JdbcUpdateSqlBuilder {

    private QuerySqlBuilder querySqlBuilder;
    private JdbcUpdateSqlBuilder updateSqlBuilder;

    /// 根据数据源设置SQL方言构建器
    ///
    /// @param dataSource 数据源
    /// @return 当前实例，支持链式调用
    /// @throws SQLException SQL异常
    public SqlDialectSelector setByDataSource(DataSource dataSource) throws SQLException {
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        String driverName = metaData.getDriverName().toLowerCase();
        SqlDialect dialect;

        if (driverName.contains("mysql") || driverName.contains("maria")) {
            dialect = SqlDialect.MYSQL;
        } else if (driverName.contains("mssql") || driverName.contains("sql server")) {
            dialect = SqlDialect.SQL_SERVER;
        } else if (driverName.contains("postgresql")) {
            dialect = SqlDialect.POSTGRESQL;
        } else {
            // 默认使用 MySQL 方言
            dialect = SqlDialect.MYSQL;
        }

        querySqlBuilder = new QuerySqlBuilderImpl(dialect);
        updateSqlBuilder = new DefaultUpdateSqlBuilder(dialect);
        return this;
    }

    /// 设置查询SQL构建器
    ///
    /// @param querySqlBuilder 查询SQL构建器
    /// @return 当前实例，支持链式调用
    public SqlDialectSelector setQuerySqlBuilder(QuerySqlBuilder querySqlBuilder) {
        this.querySqlBuilder = querySqlBuilder;
        return this;
    }

    /// 设置更新SQL构建器
    ///
    /// @param updateSqlBuilder 更新SQL构建器
    /// @return 当前实例，支持链式调用
    public SqlDialectSelector setUpdateSqlBuilder(JdbcUpdateSqlBuilder updateSqlBuilder) {
        this.updateSqlBuilder = updateSqlBuilder;
        return this;
    }

    /// 构建查询SQL语句
    ///
    /// @param context 查询上下文
    /// @return 查询SQL语句对象
    @Override
    public QuerySqlStatement build(QueryContext context) {
        return querySqlBuilder.build(context);
    }


    /// 构建插入SQL语句
    ///
    /// @param entities 实体集合
    /// @param entityType 实体类型
    /// @return 插入SQL语句列表
    @Override
    public List<InsertSqlStatement> buildInsertStatement(Iterable<?> entities, EntityType entityType) {
        return updateSqlBuilder.buildInsertStatement(entities, entityType);
    }

    /// 构建更新SQL语句
    ///
    /// @param entities 实体集合
    /// @param entityType 实体类型
    /// @param excludeNull 是否排除空值
    /// @return 批量SQL语句对象
    @Override
    public BatchSqlStatement buildUpdateStatement(Iterable<?> entities, EntitySchema entityType, boolean excludeNull) {
        return updateSqlBuilder.buildUpdateStatement(entities, entityType, excludeNull);
    }

    /// 构建删除SQL语句
    ///
    /// @param entities 实体集合
    /// @param entity 实体类型
    /// @return 批量SQL语句对象
    @Override
    public BatchSqlStatement buildDeleteStatement(Iterable<?> entities, EntityType entity) {
        return updateSqlBuilder.buildDeleteStatement(entities, entity);
    }
}