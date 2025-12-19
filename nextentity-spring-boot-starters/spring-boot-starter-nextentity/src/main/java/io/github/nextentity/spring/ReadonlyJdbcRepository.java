package io.github.nextentity.spring;

import io.github.nextentity.api.Select;
import io.github.nextentity.core.SelectImpl;
import io.github.nextentity.core.SimpleQueryConfig;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jdbc.ConnectionProvider;
import io.github.nextentity.jdbc.JdbcQueryExecutor;
import io.github.nextentity.jdbc.JdbcResultCollector;
import io.github.nextentity.jdbc.SqlDialectSelector;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.SQLException;


public class ReadonlyJdbcRepository<T, ID extends Serializable> {

    protected final Select<T> select;
    protected final Class<T> entityType;
    protected final Class<ID> idType;

    public ReadonlyJdbcRepository(DataSourceProperties properties) throws SQLException {
        this(properties.initializeDataSourceBuilder().build());
    }

    public ReadonlyJdbcRepository(DataSource dataSource) throws SQLException {
        Assert.notNull(dataSource, "dataSource cannot be null");
        ResolvableType type = ResolvableType.forClass(getClass()).as(ReadonlyJdbcRepository.class);
        this.entityType = TypeCastUtil.cast(type.resolveGeneric(0));
        if (entityType == null) {
            throw new RuntimeException();
        }
        idType = TypeCastUtil.cast(type.resolveGeneric(1));
        EntityType entity = JpaMetamodel.of().getEntity(entityType);
        if (idType != entity.id().type()) {
            throw new RuntimeException();
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        SimpleQueryConfig config = new SimpleQueryConfig();
        SqlDialectSelector querySqlBuilder = new SqlDialectSelector().setByDataSource(dataSource);
        ConnectionProvider connectionProvider = new JdbcTemplateConnectionProvider(jdbcTemplate);
        Metamodel metamodel = JpaMetamodel.of();
        JdbcQueryExecutor executor = new JdbcQueryExecutor(metamodel, querySqlBuilder, connectionProvider, new JdbcResultCollector());
        config.metamodel(metamodel).queryExecutor(executor);
        select = new SelectImpl<>(config, entityType);
    }

}
