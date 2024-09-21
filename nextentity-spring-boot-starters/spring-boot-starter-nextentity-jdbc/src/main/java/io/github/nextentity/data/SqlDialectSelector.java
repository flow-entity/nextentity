package io.github.nextentity.data;

import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.jdbc.JdbcQueryExecutor.QuerySqlBuilder;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @author HuangChengwei
 * @since 2024/4/11 下午3:35
 */
public class SqlDialectSelector implements QuerySqlBuilder, JdbcUpdateSqlBuilder, InitializingBean {

    private QuerySqlBuilder querySqlBuilder;
    private JdbcUpdateSqlBuilder updateSqlBuilder;

    public SqlDialectSelector setByDataSource(DataSource dataSource) throws SQLException {
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        String driverName = metaData.getDriverName().toLowerCase();
        if (driverName.contains("mysql") || driverName.contains("maria")) {
            querySqlBuilder = new MySqlQuerySqlBuilder();
            updateSqlBuilder = new MysqlUpdateSqlBuilder();
        } else if (driverName.contains("mssql") || driverName.contains("sql server")) {
            querySqlBuilder = new SqlServerQuerySqlBuilder();
            updateSqlBuilder = new SqlServerUpdateSqlBuilder();
        } else if (driverName.contains("postgresql")) {
            querySqlBuilder = new PostgresqlQuerySqlBuilder();
            updateSqlBuilder = new PostgresqlUpdateSqlBuilder();
        }
        return this;
    }

    @Override
    public void afterPropertiesSet() {
        Objects.requireNonNull(querySqlBuilder);
        Objects.requireNonNull(updateSqlBuilder);
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
