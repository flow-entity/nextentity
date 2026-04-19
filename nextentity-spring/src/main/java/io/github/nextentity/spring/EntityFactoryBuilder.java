package io.github.nextentity.spring;

import io.github.nextentity.core.EntityOperationsFactory;
import io.github.nextentity.core.LoggingConfig;
import io.github.nextentity.core.PaginationConfig;
import io.github.nextentity.core.PersistExecutor;
import io.github.nextentity.core.SqlLogger;
import io.github.nextentity.core.TransactionOperations;
import io.github.nextentity.core.TransactionUpdateExecutor;
import io.github.nextentity.core.configuration.DefaultPersistConfiguration;
import io.github.nextentity.core.configuration.DefaultQueryConfiguration;
import io.github.nextentity.core.configuration.PersistConfiguration;
import io.github.nextentity.core.configuration.PostProcessor;
import io.github.nextentity.core.configuration.QueryConfiguration;
import io.github.nextentity.core.exception.SqlException;
import io.github.nextentity.jdbc.ConnectionProvider;
import io.github.nextentity.jdbc.JdbcConfig;
import io.github.nextentity.jdbc.JdbcEntityOperationsFactory;
import io.github.nextentity.jdbc.SqlDialect;
import io.github.nextentity.jdbc.configuration.JdbcEntityOperationsConfiguration;
import io.github.nextentity.jpa.JpaConfig;
import io.github.nextentity.jpa.JpaEntityOperationsFactory;
import io.github.nextentity.jpa.configuration.JpaEntityOperationsConfiguration;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;

public final class EntityFactoryBuilder {

    private EntityFactoryBuilder() {
    }

    /// 创建基于 JDBC 的 NextEntity 工厂。
    ///
    /// @param jdbcTemplate Spring JDBC 模板
    /// @param template     Spring 事务模板
    /// @return JDBC 方式的 NextEntity 工厂实例
    /// @throws SqlException 如果无法确定数据库类型
    /// @see #jdbc(JdbcTemplate, SqlDialect, NextEntityProperties, TransactionTemplate)
    public static EntityOperationsFactory jdbc(JdbcTemplate jdbcTemplate, TransactionTemplate template) {
        SqlDialect sqlDialect = detectSqlDialect(jdbcTemplate);
        return jdbc(jdbcTemplate, sqlDialect, new NextEntityProperties(), template);
    }

    /// 创建基于 JDBC 的 NextEntity 工厂。
    ///
    /// @param jdbcTemplate Spring JDBC 模板
    /// @param sqlDialect   SQL 方言
    /// @param properties   配置属性
    /// @param template     Spring 事务模板
    /// @return JDBC 方式的 NextEntity 工厂实例
    public static EntityOperationsFactory jdbc(JdbcTemplate jdbcTemplate,
                                                SqlDialect sqlDialect,
                                                NextEntityProperties properties,
                                                TransactionTemplate template) {
        applyLoggingConfig(properties);

        ConnectionProvider connectionProvider = createConnectionProvider(jdbcTemplate);
        JdbcConfig jdbcConfig = toJdbcConfig(properties.getJdbc());
        PersistConfiguration persistConfig = createPersistConfiguration(template);
        QueryConfiguration queryConfig = createQueryConfiguration(properties);

        JdbcEntityOperationsConfiguration config = JdbcEntityOperationsConfiguration.builder()
                .sqlDialect(sqlDialect)
                .connectionProvider(connectionProvider)
                .jdbcConfig(jdbcConfig)
                .persistConfiguration(persistConfig)
                .queryConfiguration(queryConfig)
                .build();

        return new JdbcEntityOperationsFactory(config);
    }

    /// 创建基于 JPA 的 NextEntity 工厂。
    ///
    /// @param entityManager JPA 实体管理器
    /// @param jdbcTemplate  Spring JDBC 模板
    /// @param template      Spring 事务模板
    /// @return JPA 方式的 NextEntity 工厂实例
    /// @throws SqlException 如果无法确定数据库类型
    /// @see #jpa(EntityManager, JdbcTemplate, SqlDialect, NextEntityProperties, TransactionTemplate)
    public static EntityOperationsFactory jpa(EntityManager entityManager,
                                                JdbcTemplate jdbcTemplate,
                                                TransactionTemplate template) {
        SqlDialect sqlDialect = detectSqlDialect(jdbcTemplate);
        return jpa(entityManager, jdbcTemplate, sqlDialect, new NextEntityProperties(), template);
    }

    /// 创建基于 JPA 的 NextEntity 工厂。
    ///
    /// @param entityManager JPA 实体管理器
    /// @param jdbcTemplate  Spring JDBC 模板
    /// @param sqlDialect    SQL 方言
    /// @param properties    配置属性
    /// @param template      Spring 事务模板
    /// @return JPA 方式的 NextEntity 工厂实例
    public static EntityOperationsFactory jpa(EntityManager entityManager,
                                                JdbcTemplate jdbcTemplate,
                                                SqlDialect sqlDialect,
                                                NextEntityProperties properties,
                                                TransactionTemplate template) {
        applyLoggingConfig(properties);

        JdbcConfig jdbcConfig = toJdbcConfig(properties.getJdbc());
        JpaConfig jpaConfig = toJpaConfig(properties.getJpa());
        PersistConfiguration persistConfig = createPersistConfiguration(template);
        QueryConfiguration queryConfig = createQueryConfiguration(properties);

        JpaEntityOperationsConfiguration config = JpaEntityOperationsConfiguration.builder()
                .sqlDialect(sqlDialect)
                .entityManager(entityManager)
                .jpaConfig(jpaConfig)
                .persistConfiguration(persistConfig)
                .queryConfiguration(queryConfig)
                .build();

        return new JpaEntityOperationsFactory(config);
    }

    // ===================== Private Helper Methods =====================

    private static SqlDialect detectSqlDialect(JdbcTemplate jdbcTemplate) throws SqlException {
        DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
        try {
            return SqlDialect.detectFromDataSource(dataSource);
        } catch (SQLException e) {
            throw new SqlException(e);
        }
    }

    private static ConnectionProvider createConnectionProvider(JdbcTemplate jdbcTemplate) {
        return new ConnectionProvider() {
            @Override
            public <T> T execute(ConnectionCallback<T> action) {
                return jdbcTemplate.execute(action::doInConnection);
            }
        };
    }

    private static PersistConfiguration createPersistConfiguration(TransactionTemplate template) {
        return DefaultPersistConfiguration.builder()
                .addPostProcessor(createTransactionPostProcessor(template))
                .build();
    }

    private static PostProcessor<PersistExecutor> createTransactionPostProcessor(TransactionTemplate template) {
        return executor -> new TransactionUpdateExecutor(executor, new SpringTransactionOperations(template));
    }

    private static QueryConfiguration createQueryConfiguration(NextEntityProperties properties) {
        PaginationConfig paginationConfig = properties.getPagination().toConfig();
        return DefaultQueryConfiguration.builder()
                .paginationConfig(paginationConfig)
                .build();
    }

    private static void applyLoggingConfig(NextEntityProperties properties) {
        LoggingConfig config = toLoggingConfig(properties.getLogging());
        SqlLogger.setConfig(config);
    }

    // ===================== Configuration Conversion =====================

    private static JdbcConfig toJdbcConfig(JdbcProperties props) {
        return JdbcConfig.builder()
                .queryTimeout(props.getQuery().getTimeout())
                .fetchSize(props.getQuery().getFetchSize())
                .inlineNumericLiterals(props.getQuery().isInlineNumericLiterals())
                .batchEnabled(props.getBatch().isEnabled())
                .batchSize(props.getBatch().getSize())
                .returnGeneratedKeys(props.getInsert().isReturnGeneratedKeys())
                .build();
    }

    private static JpaConfig toJpaConfig(JpaProperties props) {
        return JpaConfig.builder()
                .stringParameterBinding(props.isStringParameterBinding())
                .nativeSubqueries(props.isNativeSubqueries())
                .build();
    }

    private static LoggingConfig toLoggingConfig(LoggingProperties props) {
        return LoggingConfig.builder()
                .enabled(props.getSql().isEnabled())
                .parameters(props.getSql().isParameters())
                .loggerName(props.getSql().getLoggerName())
                .build();
    }

}