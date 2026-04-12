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

    private EntityFactoryBuilder() {
    }

    /// 创建基于 JDBC 的 NextEntity 工厂。
    ///
    /// @param jdbcTemplate Spring JDBC 模板
    /// @param template     Spring 事务模板
    /// @return JDBC 方式的 NextEntity 工厂实例
    /// @throws SqlException 如果无法确定数据库类型
    /// @see #jdbc(JdbcTemplate, SqlDialect, NextEntityProperties, TransactionTemplate)
    public static EntityTemplateFactory jdbc(JdbcTemplate jdbcTemplate, TransactionTemplate template) {
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
    public static EntityTemplateFactory jdbc(JdbcTemplate jdbcTemplate,
                                             SqlDialect sqlDialect,
                                             NextEntityProperties properties,
                                             TransactionTemplate template) {
        FactoryContext ctx = new FactoryContext(jdbcTemplate, sqlDialect, properties, template);
        return buildJdbcFactory(ctx);
    }

    /// 创建基于 JPA 的 NextEntity 工厂。
    ///
    /// @param entityManager JPA 实体管理器
    /// @param jdbcTemplate  Spring JDBC 模板
    /// @param template      Spring 事务模板
    /// @return JPA 方式的 NextEntity 工厂实例
    /// @throws SqlException 如果无法确定数据库类型
    /// @see #jpa(EntityManager, JdbcTemplate, SqlDialect, NextEntityProperties, TransactionTemplate)
    public static EntityTemplateFactory jpa(EntityManager entityManager,
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
    public static EntityTemplateFactory jpa(EntityManager entityManager,
                                            JdbcTemplate jdbcTemplate,
                                            SqlDialect sqlDialect,
                                            NextEntityProperties properties,
                                            TransactionTemplate template) {
        FactoryContext ctx = new FactoryContext(jdbcTemplate, sqlDialect, properties, template);
        return buildJpaFactory(ctx, entityManager);
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

    private static void applyCommonConfig(NextEntityProperties properties) {
        applyLoggingConfig(properties);
        PaginationConfig paginationConfig = properties.getPagination().toConfig();
        paginationConfig.apply();
    }

    private static EntityTemplateFactory buildJdbcFactory(FactoryContext ctx) {
        applyCommonConfig(ctx.properties);

        Metamodel metamodel = JpaMetamodel.of();
        ConnectionProvider connectionProvider = createConnectionProvider(ctx.jdbcTemplate);
        JdbcConfig jdbcConfig = toJdbcConfig(ctx.properties.getJdbc());
        SqlBuilder sqlBuilder = SqlBuilder.of(ctx.sqlDialect, jdbcConfig);

        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(
                metamodel, sqlBuilder, connectionProvider, new JdbcResultCollector(), jdbcConfig);

        SpringTransactionOperations sto = new SpringTransactionOperations(ctx.template);
        PersistExecutor jdbcUpdateExecutor = new JdbcPersistExecutor(
                sqlBuilder, connectionProvider, jdbcConfig);
        jdbcUpdateExecutor = new TransactionUpdateExecutor(jdbcUpdateExecutor, sto);

        return new EntityTemplateFactory(
                metamodel, jdbcQueryExecutor, jdbcUpdateExecutor, ctx.properties.getPagination().toConfig());
    }

    private static EntityTemplateFactory buildJpaFactory(FactoryContext ctx, EntityManager entityManager) {
        applyCommonConfig(ctx.properties);

        Metamodel metamodel = JpaMetamodel.of();
        ConnectionProvider connectionProvider = createConnectionProvider(ctx.jdbcTemplate);
        JdbcConfig jdbcConfig = toJdbcConfig(ctx.properties.getJdbc());
        JpaConfig jpaConfig = toJpaConfig(ctx.properties.getJpa());
        SqlBuilder sqlBuilder = SqlBuilder.of(ctx.sqlDialect, jdbcConfig);

        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(
                metamodel, sqlBuilder, connectionProvider, new JdbcResultCollector(), jdbcConfig);

        JpaQueryExecutor jpaQueryExecutor = new JpaQueryExecutor(
                entityManager, metamodel, jdbcQueryExecutor, jpaConfig);

        SpringTransactionOperations sto = new SpringTransactionOperations(ctx.template);
        PersistExecutor jpaUpdateExecutor = new TransactionUpdateExecutor(
                new JpaPersistExecutor(entityManager), sto);

        return new EntityTemplateFactory(metamodel, jpaQueryExecutor, jpaUpdateExecutor,
                ctx.properties.getPagination().toConfig());
    }

    // ===================== Configuration Conversion =====================

    private static void applyLoggingConfig(NextEntityProperties properties) {
        LoggingConfig config = toLoggingConfig(properties.getLogging());
        SqlLogger.setConfig(config);
    }

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

    // ===================== Internal Context Class =====================

    private static class FactoryContext {
        final JdbcTemplate jdbcTemplate;
        final SqlDialect sqlDialect;
        final NextEntityProperties properties;
        final TransactionTemplate template;

        FactoryContext(JdbcTemplate jdbcTemplate, SqlDialect sqlDialect,
                       NextEntityProperties properties, TransactionTemplate template) {
            this.jdbcTemplate = jdbcTemplate;
            this.sqlDialect = sqlDialect;
            this.properties = properties;
            this.template = template;
        }
    }

}