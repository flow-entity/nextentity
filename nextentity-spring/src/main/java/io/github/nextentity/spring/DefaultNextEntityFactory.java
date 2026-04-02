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

public record DefaultNextEntityFactory(
        Metamodel metamodel,
        QueryExecutor queryExecutor,
        UpdateExecutor updateExecutor,
        @Nullable EntityManager entityManager,
        @Nullable ConnectionProvider connectionProvider,
        @Nullable SqlDialect sqlDialect
) implements NextEntityFactory {

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


    public static class NoneTransactionProvider implements ConnectionProvider, JpaTransactionTemplate {
        private final JdbcTemplate jdbcTemplate;


        public NoneTransactionProvider(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public <T> T execute(ConnectionCallback<T> action) {
            return jdbcTemplate.execute(action::doInConnection);
        }

        @Override
        public <T> T executeInTransaction(ConnectionCallback<T> action) {
            return execute(action);
        }

        @Override
        public <T> T executeInTransaction(EntityManager entityManager, Supplier<T> action) {
            return action.get();
        }
    }

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

    private static SqlDialect detectSqlDialect(SqlDialectSelector selector) {
        // Default to MySQL dialect, actual detection would require more information
        return SqlDialect.MYSQL;
    }

    @Override
    public <T> QueryBuilder<T> queryBuilder(Class<T> entityType) {
        return new DefaultQueryBuilder<>(metamodel, queryExecutor, entityType);
    }

    @Override
    public UpdateExecutor updateExecutor() {
        return updateExecutor;
    }

    @Override
    public <T> UpdateWhereStep<T> updateWhereStep(Class<T> entityType) {
        if (entityManager != null) {
            return new JpaUpdateWhereStep<>(entityType, metamodel, entityManager);
        } else if (connectionProvider != null && sqlDialect != null) {
            return new JdbcUpdateWhereStep<>(entityType, metamodel, updateExecutor, connectionProvider, sqlDialect);
        }
        throw new IllegalStateException("Neither JPA nor JDBC is properly configured");
    }

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
