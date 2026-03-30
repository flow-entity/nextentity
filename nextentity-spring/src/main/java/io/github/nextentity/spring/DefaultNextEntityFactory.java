package io.github.nextentity.spring;

import io.github.nextentity.api.Select;
import io.github.nextentity.core.QueryBuilder;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.exception.SqlException;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.jpa.JpaQueryExecutor;
import io.github.nextentity.jpa.JpaTransactionTemplate;
import io.github.nextentity.jpa.JpaUpdateExecutor;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

public record DefaultNextEntityFactory(
        Metamodel metamodel,
        QueryExecutor queryExecutor,
        UpdateExecutor updateExecutor
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
        return new DefaultNextEntityFactory(
                metamodel,
                jdbcQueryExecutor,
                jdbcUpdateExecutor
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
                jpaUpdateExecutor
        );
    }

    @Override
    public <T> Select<T> queryBuilder(Class<T> entityType) {
        return new QueryBuilder<>(metamodel, queryExecutor, entityType);
    }

    @Override
    public UpdateExecutor updateExecutor() {
        return updateExecutor;
    }
}
