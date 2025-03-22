package io.github.nextentity.spring;

import io.github.nextentity.core.QueryPostProcessor;
import io.github.nextentity.core.RepositoryFactory;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.converter.TypeConverter;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Getter
public class JdbcRepositoryFactoryConfiguration implements InitializingBean {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired(required = false)
    private QueryPostProcessor queryPostProcessor;
    private RepositoryFactory repositoryFactory;


    protected QueryPostProcessor getQueryPostProcessor() {
        return queryPostProcessor;
    }

    protected Metamodel metamodel() {
        return JpaMetamodel.of();
    }

    protected List<TypeConverter> typeConverter() {
        return List.of(TypeConverter.ofDefault());
    }

    protected SqlDialectSelector sqlDialectAutoSelector(DataSource dataSource) throws SQLException {
        return new SqlDialectSelector().setByDataSource(dataSource);
    }

    protected JdbcQueryExecutor jdbcQueryExecutor(Metamodel metamodel,
                                                  JdbcQueryExecutor.QuerySqlBuilder querySqlBuilder,
                                                  JdbcQueryExecutor.ResultCollector resultCollector,
                                                  ConnectionProvider connectionProvider) {
        return new JdbcQueryExecutor(metamodel, querySqlBuilder, connectionProvider, resultCollector);
    }

    protected RepositoryFactory jdbcEntitiesFactory(JdbcQueryExecutor queryExecutor,
                                                    UpdateExecutor updateExecutor,
                                                    QueryPostProcessor queryPostProcessor,
                                                    Metamodel metamodel) {
        return new RepositoryFactory(queryExecutor, updateExecutor, queryPostProcessor, metamodel);
    }

    protected JdbcQueryExecutor.ResultCollector jdbcResultCollector(List<TypeConverter> typeConverters) {
        return new JdbcResultCollector(TypeConverter.of(typeConverters));
    }

    protected ConnectionProvider connectionProvider(JdbcTemplate jdbcTemplate) {
        return new ConnectionProvider() {
            @Override
            public <T> T execute(ConnectionCallback<T> action) {
                return jdbcTemplate.execute(action::doInConnection);
            }
        };
    }

    protected UpdateExecutor jdbcUpdate(JdbcUpdateSqlBuilder sqlBuilder,
                                        ConnectionProvider connectionProvider,
                                        Metamodel metamodel) {
        return new JdbcUpdateExecutor(sqlBuilder, connectionProvider, metamodel);
    }

    private <T> T getOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SqlDialectSelector querySqlBuilder = sqlDialectAutoSelector(jdbcTemplate.getDataSource());
        ConnectionProvider connectionProvider = connectionProvider(jdbcTemplate);
        Metamodel metamodel = metamodel();
        JdbcQueryExecutor executor = jdbcQueryExecutor(
                metamodel,
                querySqlBuilder,
                jdbcResultCollector(typeConverter()),
                connectionProvider
        );
        UpdateExecutor updateExecutor = jdbcUpdate(querySqlBuilder, connectionProvider, metamodel);
        repositoryFactory = jdbcEntitiesFactory(executor, updateExecutor, getQueryPostProcessor(), metamodel);
    }
}
