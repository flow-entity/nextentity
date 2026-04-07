package io.github.nextentity.spring;

import io.github.nextentity.api.QueryBuilder;
import io.github.nextentity.core.DefaultQueryBuilder;
import io.github.nextentity.core.LoggingConfig;
import io.github.nextentity.core.PaginationConfig;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.SqlLogger;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.exception.SqlException;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.jpa.JpaConfig;
import io.github.nextentity.jpa.JpaQueryExecutor;
import io.github.nextentity.jpa.JpaTransactionTemplate;
import io.github.nextentity.jpa.JpaUpdateExecutor;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

/// 默认的 NextEntity 上下文实现。
///
/// 该类实现了 NextEntityContext 接口，提供创建 QueryBuilder
/// 和获取 UpdateExecutor、Metamodel 的能力。
///
/// 支持两种数据库访问模式：
/// - JDBC 模式：纯 JDBC 操作，适合轻量级场景
/// - JPA 模式：结合 JPA 和 JDBC，适合需要 JPA 特性的场景
///
/// 使用示例：
/// ```java
/// // JDBC 模式
/// NextEntityContext context = DefaultNextEntityContext.jdbc(jdbcTemplate, dialect, properties);
///
/// // JPA 模式
/// NextEntityContext context = DefaultNextEntityContext.jpa(entityManager, jdbcTemplate, dialect, properties);
/// ```
///
/// @param metamodel        实体元模型，提供实体结构信息
/// @param queryExecutor    查询执行器，用于执行 SELECT 查询
/// @param updateExecutor   更新执行器，用于执行 INSERT、UPDATE、DELETE 操作
/// @param entityManager    JPA 实体管理器（JPA 模式时使用，JDBC 模式为 null）
/// @param paginationConfig 分页配置
/// @author HuangChengwei
/// @since 1.0.0
public record DefaultNextEntityContext(
        Metamodel metamodel,
        QueryExecutor queryExecutor,
        UpdateExecutor updateExecutor,
        @Nullable
        EntityManager entityManager,
        PaginationConfig paginationConfig
) implements NextEntityContext {

    /// 创建基于 JDBC 的 NextEntity 工厂。
    ///
    /// @param jdbcTemplate Spring JDBC 模板
    /// @return JDBC 方式的 NextEntity 工厂实例
    /// @throws SqlException 如果无法确定数据库类型
    public static DefaultNextEntityContext jdbc(JdbcTemplate jdbcTemplate) {
        DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
        SqlDialect sqlDialect;
        try {
            sqlDialect = SqlDialect.detectFromDataSource(dataSource);
        } catch (SQLException e) {
            throw new SqlException(e);
        }
        return jdbc(jdbcTemplate, sqlDialect, new NextEntityProperties());
    }

    /// 创建基于 JDBC 的 NextEntity 工厂。
    ///
    /// @param jdbcTemplate Spring JDBC 模板
    /// @param sqlDialect   SQL 方言
    /// @param properties   配置属性
    /// @return JDBC 方式的 NextEntity 工厂实例
    public static DefaultNextEntityContext jdbc(JdbcTemplate jdbcTemplate,
                                                SqlDialect sqlDialect,
                                                NextEntityProperties properties) {
        // 应用日志配置
        applyLoggingConfig(properties);

        // 创建分页配置并打印信息
        PaginationConfig paginationConfig = properties.getPagination().toConfig();
        paginationConfig.apply();

        Metamodel metamodel = JpaMetamodel.of();
        ConnectionProvider connectionProvider = new NoneTransactionProvider(jdbcTemplate);

        // 从配置属性创建 JdbcConfig
        JdbcConfig jdbcConfig = toJdbcConfig(properties.getJdbc());
        SqlBuilder sqlBuilder = SqlBuilder.of(sqlDialect, jdbcConfig);

        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(
                metamodel, sqlBuilder, connectionProvider, new JdbcResultCollector(), jdbcConfig);
        JdbcUpdateExecutor jdbcUpdateExecutor = new JdbcUpdateExecutor(
                sqlBuilder, connectionProvider, metamodel, jdbcConfig);

        return new DefaultNextEntityContext(
                metamodel,
                jdbcQueryExecutor,
                jdbcUpdateExecutor,
                null,
                paginationConfig
        );
    }

    /// 创建基于 JPA 的 NextEntity 工厂。
    ///
    /// @param entityManager JPA 实体管理器
    /// @param jdbcTemplate  Spring JDBC 模板
    /// @return JPA 方式的 NextEntity 工厂实例
    /// @throws SqlException 如果无法确定数据库类型
    public static DefaultNextEntityContext jpa(EntityManager entityManager,
                                               JdbcTemplate jdbcTemplate) {
        DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
        SqlDialect sqlDialect;
        try {
            sqlDialect = SqlDialect.detectFromDataSource(dataSource);
        } catch (SQLException e) {
            throw new SqlException(e);
        }
        return jpa(entityManager, jdbcTemplate, sqlDialect, new NextEntityProperties());
    }

    /// 创建基于 JPA 的 NextEntity 工厂。
    ///
    /// @param entityManager JPA 实体管理器
    /// @param jdbcTemplate  Spring JDBC 模板
    /// @param sqlDialect    SQL 方言
    /// @param properties    配置属性
    /// @return JPA 方式的 NextEntity 工厂实例
    public static DefaultNextEntityContext jpa(EntityManager entityManager,
                                               JdbcTemplate jdbcTemplate,
                                               SqlDialect sqlDialect,
                                               NextEntityProperties properties) {
        // 应用日志配置
        applyLoggingConfig(properties);

        // 创建分页配置并打印信息
        PaginationConfig paginationConfig = properties.getPagination().toConfig();
        paginationConfig.apply();

        Metamodel metamodel = JpaMetamodel.of();
        NoneTransactionProvider noneTransactionProvider = new NoneTransactionProvider(jdbcTemplate);

        // 从配置属性创建配置对象
        JdbcConfig jdbcConfig = toJdbcConfig(properties.getJdbc());
        JpaConfig jpaConfig = toJpaConfig(properties.getJpa());
        SqlBuilder sqlBuilder = SqlBuilder.of(sqlDialect, jdbcConfig);

        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(
                metamodel, sqlBuilder, noneTransactionProvider, new JdbcResultCollector(), jdbcConfig);

        JpaQueryExecutor jpaQueryExecutor = new JpaQueryExecutor(
                entityManager, metamodel, jdbcQueryExecutor, jpaConfig);
        JpaUpdateExecutor jpaUpdateExecutor = new JpaUpdateExecutor(
                entityManager, metamodel, noneTransactionProvider);

        return new DefaultNextEntityContext(
                metamodel,
                jpaQueryExecutor,
                jpaUpdateExecutor,
                entityManager,
                paginationConfig
        );
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

    /// 无事务管理的连接提供者。
    ///
    /// 该类实现了 ConnectionProvider 和 JpaTransactionTemplate 接口，
    /// 使用 Spring 的 JdbcTemplate 执行数据库操作。
    /// 事务管理依赖 Spring 的声明式事务（@Transactional）。
    public static class NoneTransactionProvider implements ConnectionProvider, JpaTransactionTemplate {
        private final JdbcTemplate jdbcTemplate;

        /// 创建无事务管理的连接提供者。
        ///
        /// @param jdbcTemplate Spring JDBC 模板
        public NoneTransactionProvider(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        /// 使用数据库连接执行操作。
        ///
        /// @param action 连接回调操作
        /// @param <T>    操作返回类型
        /// @return 操作结果
        @Override
        public <T> T execute(ConnectionCallback<T> action) {
            return jdbcTemplate.execute(action::doInConnection);
        }

        /// 在事务中执行连接操作（实际无事务管理）。
        ///
        /// 由于使用 Spring 声明式事务，此方法直接执行操作。
        /// 实际的事务由调用方的 @Transactional 注解管理。
        ///
        /// @param action 连接回调操作
        /// @param <T>    操作返回类型
        /// @return 操作结果
        @Override
        public <T> T executeInTransaction(ConnectionCallback<T> action) {
            return execute(action);
        }

        /// 在 JPA 事务中执行操作（实际无事务管理）。
        ///
        /// 由于使用 Spring 声明式事务，此方法直接执行操作。
        ///
        /// @param entityManager JPA 实体管理器（未使用）
        /// @param action        要执行的操作
        /// @param <T>           操作返回类型
        /// @return 操作结果
        @Override
        public <T> T executeInTransaction(EntityManager entityManager, Supplier<T> action) {
            return action.get();
        }
    }

    /// 创建指定实体类型的查询构建器。
    ///
    /// @param entityType 实体类型
    /// @param <T>        实体类型参数
    /// @return 查询构建器实例
    @Override
    public <T> QueryBuilder<T> createQueryBuilder(Class<T> entityType) {
        return new DefaultQueryBuilder<>(new io.github.nextentity.core.SimpleQueryContext<>(
                metamodel, queryExecutor, paginationConfig, metamodel.getEntity(entityType), entityType));
    }

    /// 获取更新执行器。
    ///
    /// @return 更新执行器实例
    @Override
    public UpdateExecutor getUpdateExecutor() {
        return updateExecutor;
    }

    @Override
    public Metamodel getMetamodel() {
        return metamodel;
    }
}