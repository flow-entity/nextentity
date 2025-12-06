package io.github.nextentity.data.jdbc;

import io.github.nextentity.core.QueryPostProcessor;
import io.github.nextentity.core.RepositoryFactory;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.converter.TypeConverter;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.data.TransactionalUpdateExecutor;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.jdbc.JdbcQueryExecutor.QuerySqlBuilder;
import io.github.nextentity.jdbc.JdbcQueryExecutor.ResultCollector;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class NextEntityJdbcAutoConfiguration {

    public static final String JDBC_REPOSITORY_FACTORY_BEAN_NAME = "jdbcRepositoryFactory";

    @Bean(name = JDBC_REPOSITORY_FACTORY_BEAN_NAME)
    protected RepositoryFactory jdbcEntitiesFactory(JdbcQueryExecutor queryExecutor,
                                                    UpdateExecutor updateExecutor,
                                                    @Autowired(required = false)
                                                    QueryPostProcessor queryPostProcessor,
                                                    Metamodel metamodel) {
        return new RepositoryFactory(queryExecutor, updateExecutor, queryPostProcessor, metamodel);
    }

    @Bean
    protected JdbcQueryExecutor jdbcQueryExecutor(Metamodel metamodel,
                                                  QuerySqlBuilder querySqlBuilder,
                                                  ResultCollector resultCollector,
                                                  ConnectionProvider connectionProvider) {
        return new JdbcQueryExecutor(metamodel, querySqlBuilder, connectionProvider, resultCollector);
    }

    @Bean
    protected ConnectionProvider connectionProvider(JdbcTemplate jdbcTemplate) {
        return new JdbcConnectionProvider(jdbcTemplate);
    }

    @Bean
    protected ResultCollector jdbcResultCollector() {
        return new JdbcResultCollector();
    }

    @Bean
    protected UpdateExecutor jdbcUpdate(JdbcUpdateSqlBuilder sqlBuilder,
                                        ConnectionProvider connectionProvider,
                                        Metamodel metamodel) {
        JdbcUpdateExecutor jdbcUpdate = new JdbcUpdateExecutor(sqlBuilder, connectionProvider, metamodel);
        return new TransactionalUpdateExecutor(jdbcUpdate);
    }

    @Bean
    @ConditionalOnMissingBean({QuerySqlBuilder.class, JdbcUpdateSqlBuilder.class, SqlDialectSelector.class})
    protected SqlDialectSelector sqlDialectAutoSelector(DataSource dataSource) throws SQLException {
        return new SqlDialectSelector().setByDataSource(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    protected Metamodel jpaMetamodel() {
        return JpaMetamodel.of();
    }

    @Bean
    @ConditionalOnMissingBean
    protected TypeConverter typeConverter() {
        return TypeConverter.ofDefault();
    }

}
