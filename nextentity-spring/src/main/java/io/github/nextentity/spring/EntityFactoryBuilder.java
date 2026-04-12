package io.github.nextentity.spring;

import io.github.nextentity.core.*;
import io.github.nextentity.core.exception.SqlException;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.jpa.JpaConfig;
import io.github.nextentity.jpa.JpaPersistExecutor;
import io.github.nextentity.jpa.JpaQueryExecutor;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;

public final class EntityFactoryBuilder {

    /// 创建基于 JDBC 的 NextEntity 工厂。
    ///
    /// @param jdbcTemplate Spring JDBC 模板
    /// @return JDBC 方式的 NextEntity 工厂实例
    /// @throws SqlException 如果无法确定数据库类型
    public static EntityTemplateFactory jdbc(JdbcTemplate jdbcTemplate, TransactionTemplate template) {
        DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
        SqlDialect sqlDialect;
        try {
            sqlDialect = SqlDialect.detectFromDataSource(dataSource);
        } catch (SQLException e) {
            throw new SqlException(e);
        }
        return jdbc(jdbcTemplate, sqlDialect, new NextEntityProperties(), template);
    }

    /// 创建基于 JDBC 的 NextEntity 工厂。
    ///
    /// @param jdbcTemplate Spring JDBC 模板
    /// @param sqlDialect   SQL 方言
    /// @param properties   配置属性
    /// @return JDBC 方式的 NextEntity 工厂实例
    public static EntityTemplateFactory jdbc(JdbcTemplate jdbcTemplate,
                                             SqlDialect sqlDialect,
                                             NextEntityProperties properties, TransactionTemplate template) {
        // 应用日志配置
        applyLoggingConfig(properties);

        // 创建分页配置并打印信息
        PaginationConfig paginationConfig = properties.getPagination().toConfig();
        paginationConfig.apply();

        Metamodel metamodel = JpaMetamodel.of();
        ConnectionProvider connectionProvider = new ConnectionProvider() {
            @Override
            public <T> T execute(ConnectionCallback<T> action) {
                return jdbcTemplate.execute(action::doInConnection);
            }

            @Override
            public <T> T executeInTransaction(ConnectionCallback<T> action) {
                throw new UnsupportedOperationException();
            }

        };

        // 从配置属性创建 JdbcConfig
        JdbcConfig jdbcConfig = toJdbcConfig(properties.getJdbc());
        SqlBuilder sqlBuilder = SqlBuilder.of(sqlDialect, jdbcConfig);


        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(
                metamodel, sqlBuilder, connectionProvider, new JdbcResultCollector(), jdbcConfig);

        SpringTransactionOperations sto = new SpringTransactionOperations(template);

        PersistExecutor jdbcUpdateExecutor = new JdbcPersistExecutor(
                sqlBuilder, connectionProvider, jdbcConfig);

        jdbcUpdateExecutor = new TransactionUpdateExecutor(jdbcUpdateExecutor, sto);

        return new EntityTemplateFactory(
                metamodel, jdbcQueryExecutor, jdbcUpdateExecutor, paginationConfig);
    }

    /// 创建基于 JPA 的 NextEntity 工厂。
    ///
    /// @param entityManager JPA 实体管理器
    /// @param jdbcTemplate  Spring JDBC 模板
    /// @return JPA 方式的 NextEntity 工厂实例
    /// @throws SqlException 如果无法确定数据库类型
    public static EntityTemplateFactory jpa(EntityManager entityManager,
                                            JdbcTemplate jdbcTemplate,
                                            TransactionTemplate template) {
        DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
        SqlDialect sqlDialect;
        try {
            sqlDialect = SqlDialect.detectFromDataSource(dataSource);
        } catch (SQLException e) {
            throw new SqlException(e);
        }
        return jpa(entityManager, jdbcTemplate, sqlDialect, new NextEntityProperties(), template);
    }

    /// 创建基于 JPA 的 NextEntity 工厂。
    ///
    /// @param entityManager JPA 实体管理器
    /// @param jdbcTemplate  Spring JDBC 模板
    /// @param sqlDialect    SQL 方言
    /// @param properties    配置属性
    /// @return JPA 方式的 NextEntity 工厂实例
    public static EntityTemplateFactory jpa(EntityManager entityManager,
                                            JdbcTemplate jdbcTemplate,
                                            SqlDialect sqlDialect,
                                            NextEntityProperties properties, TransactionTemplate template) {
        // 应用日志配置
        applyLoggingConfig(properties);

        // 创建分页配置并打印信息
        PaginationConfig paginationConfig = properties.getPagination().toConfig();
        paginationConfig.apply();

        Metamodel metamodel = JpaMetamodel.of();
        ConnectionProvider connectionProvider = new ConnectionProvider() {
            @Override
            public <T> T execute(ConnectionCallback<T> action) {
                return jdbcTemplate.execute(action::doInConnection);
            }

            @Override
            public <T> T executeInTransaction(ConnectionCallback<T> action) {
                throw new UnsupportedOperationException();
            }

        };

        // 从配置属性创建配置对象
        JdbcConfig jdbcConfig = toJdbcConfig(properties.getJdbc());
        JpaConfig jpaConfig = toJpaConfig(properties.getJpa());
        SqlBuilder sqlBuilder = SqlBuilder.of(sqlDialect, jdbcConfig);

        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(
                metamodel, sqlBuilder, connectionProvider, new JdbcResultCollector(), jdbcConfig);

        JpaQueryExecutor jpaQueryExecutor = new JpaQueryExecutor(
                entityManager, metamodel, jdbcQueryExecutor, jpaConfig);
        SpringTransactionOperations sto = new SpringTransactionOperations(template);
        PersistExecutor jpaUpdateExecutor = new TransactionUpdateExecutor(new JpaPersistExecutor(entityManager), sto);

        return new EntityTemplateFactory(metamodel, jpaQueryExecutor, jpaUpdateExecutor, paginationConfig);
    }

    /// 应用日志配置
    private static void applyLoggingConfig(NextEntityProperties properties) {
        LoggingConfig config = toLoggingConfig(properties.getLogging());
        SqlLogger.setConfig(config);
    }

    /// 将 JdbcProperties 转换为 JdbcConfig
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

    /// 将 JpaProperties 转换为 JpaConfig
    private static JpaConfig toJpaConfig(JpaProperties props) {
        return JpaConfig.builder()
                .stringParameterBinding(props.isStringParameterBinding())
                .nativeSubqueries(props.isNativeSubqueries())
                .build();
    }

    /// 将 LoggingProperties 转换为 LoggingConfig
    private static LoggingConfig toLoggingConfig(LoggingProperties props) {
        return LoggingConfig.builder()
                .enabled(props.getSql().isEnabled())
                .parameters(props.getSql().isParameters())
                .loggerName(props.getSql().getLoggerName())
                .build();
    }

}