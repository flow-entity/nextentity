package io.github.nextentity.spring;

import io.github.nextentity.core.*;
import io.github.nextentity.core.configuration.PersistConfiguration;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.interceptor.ResultInterceptor;
import io.github.nextentity.core.exception.SqlException;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.jpa.JpaPersistExecutor;
import io.github.nextentity.jpa.JpaQueryExecutor;
import io.github.nextentity.jpa.JpaConfig;
import io.github.nextentity.jpa.EntityManagerConnectionProvider;
import io.github.nextentity.core.meta.impl.DefaultMetamodel;
import io.github.nextentity.core.configuration.MetamodelConfiguration;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
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
        return jdbc(jdbcTemplate, sqlDialect, properties, template, List.of(), List.of());
    }

    /// 创建基于 JDBC 的 NextEntity 工厂（含拦截器）。
    ///
    /// @param jdbcTemplate        Spring JDBC 模板
    /// @param sqlDialect          SQL 方言
    /// @param properties          配置属性
    /// @param template            Spring 事务模板
    /// @param constructInterceptors 构造拦截器列表
    /// @param resultInterceptors   结果拦截器列表
    /// @return JDBC 方式的 NextEntity 工厂实例
    public static EntityOperationsFactory jdbc(JdbcTemplate jdbcTemplate,
                                                SqlDialect sqlDialect,
                                                NextEntityProperties properties,
                                                TransactionTemplate template,
                                                List<ConstructInterceptor> constructInterceptors,
                                                List<ResultInterceptor> resultInterceptors) {
        applyLoggingConfig(properties);

        MetamodelConfiguration metamodelConfig = toMetamodelConfig(properties.getMetamodel());
        DefaultMetamodel metamodel = new DefaultMetamodel(metamodelConfig);
        ConnectionProvider connectionProvider = createConnectionProvider(jdbcTemplate);
        JdbcConfig jdbcConfig = toJdbcConfig(properties.getJdbc());

        SqlBuilder sqlBuilder = SqlBuilder.of(sqlDialect, jdbcConfig);
        InterceptorSelector<ConstructInterceptor> interceptorSelector = new InterceptorSelector<>(constructInterceptors);

        // 创建查询执行器
        QueryExecutor queryExecutor = new JdbcQueryExecutor(
                metamodel,
                sqlBuilder,
                connectionProvider,
                new JdbcResultCollector(),
                jdbcConfig,
                interceptorSelector
        );

        // 创建持久化执行器
        PersistExecutor persistExecutor = new JdbcPersistExecutor(sqlBuilder, connectionProvider, jdbcConfig);
        return getEntityTemplateFactory(properties, template, persistExecutor, metamodel, queryExecutor, interceptorSelector);
    }

    private static @NonNull EntityTemplateFactory getEntityTemplateFactory(NextEntityProperties properties,
                                                                           TransactionTemplate template,
                                                                           PersistExecutor persistExecutor,
                                                                           DefaultMetamodel metamodel,
                                                                           QueryExecutor queryExecutor,
                                                                           InterceptorSelector<ConstructInterceptor> interceptorSelector) {
        persistExecutor = applyPostProcessors(persistExecutor, template);

        // 组装工厂
        PaginationConfig paginationConfig = properties.getPagination().toConfig();
        FetchConfig fetchConfig = properties.getFetch().toFetchConfig();

        EntityTemplateFactoryConfig factoryConfig = new EntityTemplateFactoryConfig(
                metamodel, persistExecutor, queryExecutor, fetchConfig, paginationConfig, interceptorSelector
        );

        return new EntityTemplateFactory(queryExecutor, persistExecutor, factoryConfig);
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
        return jpa(entityManager, jdbcTemplate, sqlDialect, properties, template, List.of(), List.of());
    }

    /// 创建基于 JPA 的 NextEntity 工厂（含拦截器）。
    ///
    /// @param entityManager        JPA 实体管理器
    /// @param jdbcTemplate         Spring JDBC 模板
    /// @param sqlDialect           SQL 方言
    /// @param properties           配置属性
    /// @param template             Spring 事务模板
    /// @param constructInterceptors 构造拦截器列表
    /// @param resultInterceptors   结果拦截器列表
    /// @return JPA 方式的 NextEntity 工厂实例
    public static EntityOperationsFactory jpa(EntityManager entityManager,
                                                JdbcTemplate jdbcTemplate,
                                                SqlDialect sqlDialect,
                                                NextEntityProperties properties,
                                                TransactionTemplate template,
                                                List<ConstructInterceptor> constructInterceptors,
                                                List<ResultInterceptor> resultInterceptors) {
        applyLoggingConfig(properties);

        MetamodelConfiguration metamodelConfig = toMetamodelConfig(properties.getMetamodel());
        DefaultMetamodel metamodel = new DefaultMetamodel(metamodelConfig);
        JdbcConfig jdbcConfig = toJdbcConfig(properties.getJdbc());
        JpaConfig jpaConfig = toJpaConfig(properties.getJpa());
        ConnectionProvider connectionProvider = new EntityManagerConnectionProvider(entityManager);

        SqlBuilder sqlBuilder = SqlBuilder.of(sqlDialect, jdbcConfig);
        InterceptorSelector<ConstructInterceptor> interceptorSelector = new InterceptorSelector<>(constructInterceptors);

        // 创建原生 JDBC 查询执行器
        QueryExecutor nativeQueryExecutor = new JdbcQueryExecutor(
                metamodel,
                sqlBuilder,
                connectionProvider,
                new JdbcResultCollector(),
                jdbcConfig,
                interceptorSelector
        );

        // 创建 JPA 查询执行器
        QueryExecutor queryExecutor = new JpaQueryExecutor(
                entityManager, metamodel, nativeQueryExecutor, jpaConfig, interceptorSelector
        );

        // 创建持久化执行器
        PersistExecutor persistExecutor = new JpaPersistExecutor(entityManager);
        return getEntityTemplateFactory(properties, template, persistExecutor, metamodel, queryExecutor, interceptorSelector);
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

    private static PersistExecutor applyPostProcessors(PersistExecutor executor, TransactionTemplate template) {
        PersistConfiguration persistConfig = createPersistConfiguration(template);
        if (persistConfig != null) {
            for (var processor : persistConfig.getPostProcessors()) {
                executor = processor.process(executor);
            }
        }
        return executor;
    }

    private static PersistConfiguration createPersistConfiguration(TransactionTemplate template) {
        return io.github.nextentity.core.configuration.DefaultPersistConfiguration.builder()
                .addPostProcessor(createTransactionPostProcessor(template))
                .build();
    }

    private static io.github.nextentity.core.configuration.PostProcessor<PersistExecutor> createTransactionPostProcessor(TransactionTemplate template) {
        return executor -> new TransactionUpdateExecutor(executor, new SpringTransactionOperations(template));
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

    private static MetamodelConfiguration toMetamodelConfig(MetamodelProperties props) {
        return props.toMetamodelConfiguration();
    }

}
