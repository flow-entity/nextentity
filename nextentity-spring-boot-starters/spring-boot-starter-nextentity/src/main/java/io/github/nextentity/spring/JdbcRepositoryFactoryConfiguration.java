package io.github.nextentity.spring;

import io.github.nextentity.core.RepositoryFactory;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

public class JdbcRepositoryFactoryConfiguration implements InitializingBean {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private RepositoryFactory repositoryFactory;

    protected Metamodel metamodel() {
        return JpaMetamodel.of();
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
                                                    Metamodel metamodel) {
        return new RepositoryFactory(queryExecutor, updateExecutor, metamodel);
    }

    protected JdbcQueryExecutor.ResultCollector jdbcResultCollector() {
        return new JdbcResultCollector();
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

    @Override
    public void afterPropertiesSet() throws Exception {
        SqlDialectSelector querySqlBuilder = sqlDialectAutoSelector(jdbcTemplate.getDataSource());
        ConnectionProvider connectionProvider = connectionProvider(jdbcTemplate);
        Metamodel metamodel = metamodel();
        JdbcQueryExecutor executor = jdbcQueryExecutor(
                metamodel,
                querySqlBuilder,
                jdbcResultCollector(),
                connectionProvider
        );
        UpdateExecutor updateExecutor = jdbcUpdate(querySqlBuilder, connectionProvider, metamodel);
        repositoryFactory = jdbcEntitiesFactory(executor, updateExecutor, metamodel);
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    public RepositoryFactory getRepositoryFactory() {
        return this.repositoryFactory;
    }
}
