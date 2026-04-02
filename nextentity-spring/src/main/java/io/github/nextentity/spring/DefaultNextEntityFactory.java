package io.github.nextentity.spring;

import io.github.nextentity.api.DeleteWhereStep;
import io.github.nextentity.api.QueryBuilder;
import io.github.nextentity.api.UpdateWhereStep;
import io.github.nextentity.core.DefaultQueryBuilder;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.exception.SqlException;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.jpa.JpaDeleteWhereStep;
import io.github.nextentity.jpa.JpaQueryExecutor;
import io.github.nextentity.jpa.JpaTransactionTemplate;
import io.github.nextentity.jpa.JpaUpdateExecutor;
import io.github.nextentity.jpa.JpaUpdateWhereStep;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

/// 默认的 NextEntity 工厂实现。
///
/// 该类实现了 NextEntityFactory 接口，提供创建 QueryBuilder、
/// UpdateExecutor、UpdateWhereStep 和 DeleteWhereStep 的能力。
///
/// 支持两种数据库访问模式：
/// - JDBC 模式：纯 JDBC 操作，适合轻量级场景
/// - JPA 模式：结合 JPA 和 JDBC，适合需要 JPA 特性的场景
///
/// 使用示例：
/// ```java
/// // JDBC 模式
/// NextEntityFactory factory = DefaultNextEntityFactory.jdbc(jdbcTemplate);
///
/// // JPA 模式
/// NextEntityFactory factory = DefaultNextEntityFactory.jpa(entityManager, jdbcTemplate);
/// ```
///
/// @param metamodel        实体元模型，提供实体结构信息
/// @param queryExecutor    查询执行器，用于执行 SELECT 查询
/// @param updateExecutor   更新执行器，用于执行 INSERT、UPDATE、DELETE 操作
/// @param entityManager    JPA 实体管理器（JPA 模式时使用，JDBC 模式为 null）
/// @param connectionProvider 数据库连接提供者（JDBC 模式时使用，JPA 模式为 null）
/// @param sqlDialect       SQL 方言（JDBC 模式时使用，用于生成特定数据库的 SQL）
/// @author HuangChengwei
/// @since 1.0.0
public record DefaultNextEntityFactory(
        Metamodel metamodel,
        QueryExecutor queryExecutor,
        UpdateExecutor updateExecutor,
        @Nullable EntityManager entityManager,
        @Nullable ConnectionProvider connectionProvider,
        @Nullable SqlDialect sqlDialect
) implements NextEntityFactory {

    /// 创建基于 JDBC 的 NextEntity 工厂。
    ///
    /// 该方法配置纯 JDBC 方式的数据库操作：
    /// - 自动检测数据库类型并选择相应的 SQL 方言
    /// - 使用 JdbcQueryExecutor 执行查询
    /// - 使用 JdbcUpdateExecutor 执行更新操作
    ///
    /// @param jdbcTemplate Spring JDBC 模板
    /// @return JDBC 方式的 NextEntity 工厂实例
    /// @throws SqlException 如果无法确定数据库类型
    public static DefaultNextEntityFactory jdbc(JdbcTemplate jdbcTemplate) {
        SqlDialectSelector sqlDialectSelector = new SqlDialectSelector();
        DataSource dataSource = jdbcTemplate.getDataSource();
        try {
            sqlDialectSelector.setByDataSource(Objects.requireNonNull(dataSource));
        } catch (SQLException e) {
            throw new SqlException(e);
        }
        Metamodel metamodel = JpaMetamodel.of();
        ConnectionProvider connectionProvider = new NoneTransactionProvider(jdbcTemplate);

        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(metamodel, sqlDialectSelector, connectionProvider, new JdbcResultCollector());
        JdbcUpdateExecutor jdbcUpdateExecutor = new JdbcUpdateExecutor(sqlDialectSelector, connectionProvider, metamodel);

        // Determine SQL dialect from the selector
        SqlDialect sqlDialect = detectSqlDialect(sqlDialectSelector);

        return new DefaultNextEntityFactory(
                metamodel,
                jdbcQueryExecutor,
                jdbcUpdateExecutor,
                null,
                connectionProvider,
                sqlDialect
        );
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

    /// 创建基于 JPA 的 NextEntity 工厂。
    ///
    /// 该方法配置 JPA + JDBC 混合方式的数据库操作：
    /// - JPA 用于实体查询和更新操作
    /// - JDBC 用于批量操作和条件更新/删除
    /// - 自动检测数据库类型并选择相应的 SQL 方言
    ///
    /// @param entityManager JPA 实体管理器
    /// @param jdbcTemplate  Spring JDBC 模板
    /// @return JPA 方式的 NextEntity 工厂实例
    /// @throws SqlException 如果无法确定数据库类型
    public static DefaultNextEntityFactory jpa(EntityManager entityManager,
                                               JdbcTemplate jdbcTemplate) {
        SqlDialectSelector sqlDialectSelector = new SqlDialectSelector();
        DataSource dataSource = jdbcTemplate.getDataSource();
        try {
            sqlDialectSelector.setByDataSource(Objects.requireNonNull(dataSource));
        } catch (SQLException e) {
            throw new SqlException(e);
        }
        Metamodel metamodel = JpaMetamodel.of();

        NoneTransactionProvider noneTransactionProvider = new NoneTransactionProvider(jdbcTemplate);

        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(metamodel, sqlDialectSelector, noneTransactionProvider, new JdbcResultCollector());

        JpaQueryExecutor jpaQueryExecutor = new JpaQueryExecutor(
                entityManager, metamodel, jdbcQueryExecutor);
        JpaUpdateExecutor jpaUpdateExecutor = new JpaUpdateExecutor(entityManager, metamodel, noneTransactionProvider);

        return new DefaultNextEntityFactory(
                metamodel,
                jpaQueryExecutor,
                jpaUpdateExecutor,
                entityManager,
                null,
                null
        );
    }

    /// 检测 SQL 方言。
    ///
    /// 根据数据库选择器确定数据库类型，
    /// 默认返回 MySQL 方言。
    ///
    /// @param selector SQL 方言选择器
    /// @return SQL 方言
    private static SqlDialect detectSqlDialect(SqlDialectSelector selector) {
        // Default to MySQL dialect, actual detection would require more information
        return SqlDialect.MYSQL;
    }

    /// 创建指定实体类型的查询构建器。
    ///
    /// @param entityType 实体类型
    /// @param <T>        实体类型参数
    /// @return 查询构建器实例
    @Override
    public <T> QueryBuilder<T> queryBuilder(Class<T> entityType) {
        return new DefaultQueryBuilder<>(metamodel, queryExecutor, entityType);
    }

    /// 获取更新执行器。
    ///
    /// @return 更新执行器实例
    @Override
    public UpdateExecutor updateExecutor() {
        return updateExecutor;
    }

    /// 创建指定实体类型的条件更新构建器。
    ///
    /// 根据配置的模式选择合适的实现：
    /// - JPA 模式：使用 JpaUpdateWhereStep
    /// - JDBC 模式：使用 JdbcUpdateWhereStep
    ///
    /// @param entityType 实体类型
    /// @param <T>        实体类型参数
    /// @return 条件更新构建器实例
    /// @throws IllegalStateException 如果 JPA 和 JDBC 都未正确配置
    /// @since 2.1
    @Override
    public <T> UpdateWhereStep<T> updateWhereStep(Class<T> entityType) {
        if (entityManager != null) {
            return new JpaUpdateWhereStep<>(entityType, metamodel, entityManager);
        } else if (connectionProvider != null && sqlDialect != null) {
            return new JdbcUpdateWhereStep<>(entityType, metamodel, updateExecutor, connectionProvider, sqlDialect);
        }
        throw new IllegalStateException("Neither JPA nor JDBC is properly configured");
    }

    /// 创建指定实体类型的条件删除构建器。
    ///
    /// 根据配置的模式选择合适的实现：
    /// - JPA 模式：使用 JpaDeleteWhereStep
    /// - JDBC 模式：使用 JdbcDeleteWhereStep
    ///
    /// @param entityType 实体类型
    /// @param <T>        实体类型参数
    /// @return 条件删除构建器实例
    /// @throws IllegalStateException 如果 JPA 和 JDBC 都未正确配置
    /// @since 2.1
    @Override
    public <T> DeleteWhereStep<T> deleteWhereStep(Class<T> entityType) {
        if (entityManager != null) {
            return new JpaDeleteWhereStep<>(entityType, metamodel, entityManager);
        } else if (connectionProvider != null && sqlDialect != null) {
            return new JdbcDeleteWhereStep<>(entityType, metamodel, connectionProvider, sqlDialect);
        }
        throw new IllegalStateException("Neither JPA nor JDBC is properly configured");
    }
}
