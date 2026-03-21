package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.jdbc.JdbcQueryExecutor.QuerySqlBuilder;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * @author HuangChengwei
 * @since 1.0.0
 */
public class SqlDialectSelector implements QuerySqlBuilder, JdbcUpdateSqlBuilder {

    private QuerySqlBuilder querySqlBuilder;
    private JdbcUpdateSqlBuilder updateSqlBuilder;

    public SqlDialectSelector setByDataSource(DataSource dataSource) throws SQLException {
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        String driverName = metaData.getDriverName().toLowerCase();
        if (driverName.contains("mysql") || driverName.contains("maria")) {
            querySqlBuilder = new MySqlQuerySqlBuilder();
            updateSqlBuilder = new MySqlUpdateSqlBuilder();
        } else if (driverName.contains("mssql") || driverName.contains("sql server")) {
            querySqlBuilder = new SqlServerQuerySqlBuilder();
            updateSqlBuilder = new SqlServerUpdateSqlBuilder();
        } else if (driverName.contains("postgresql")) {
            querySqlBuilder = new PostgresqlQuerySqlBuilder();
            updateSqlBuilder = new PostgreSqlUpdateSqlBuilder();
        }
        return this;
    }

    public SqlDialectSelector setQuerySqlBuilder(QuerySqlBuilder querySqlBuilder) {
        this.querySqlBuilder = querySqlBuilder;
        return this;
    }

    public SqlDialectSelector setUpdateSqlBuilder(JdbcUpdateSqlBuilder updateSqlBuilder) {
        this.updateSqlBuilder = updateSqlBuilder;
        return this;
    }

    @Override
    public QuerySqlStatement build(QueryContext context) {
        return querySqlBuilder.build(context);
    }


    @Override
    public List<InsertSqlStatement> buildInsertStatement(Iterable<?> entities, EntityType entityType) {
        return updateSqlBuilder.buildInsertStatement(entities, entityType);
    }

    @Override
    public BatchSqlStatement buildUpdateStatement(Iterable<?> entities, EntitySchema entityType, boolean excludeNull) {
        return updateSqlBuilder.buildUpdateStatement(entities, entityType, excludeNull);
    }

    @Override
    public BatchSqlStatement buildDeleteStatement(Iterable<?> entities, EntityType entity) {
        return updateSqlBuilder.buildDeleteStatement(entities, entity);
    }
}
