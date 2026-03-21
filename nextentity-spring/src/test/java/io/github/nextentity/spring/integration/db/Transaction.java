package io.github.nextentity.spring.integration.db;

import io.github.nextentity.core.exception.UncheckedSQLException;
import io.github.nextentity.core.util.Exceptions;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author HuangChengwei
 */
public class Transaction {
    private static final Logger log = LoggerFactory.getLogger(Transaction.class);
    private final TransactionTemplate jdbcTransactionTemplate;
    protected DbConfig config;

    public Transaction(DbConfig config) {
        this.config = config;
        this.jdbcTransactionTemplate = jdbcTransactionTemplate();

    }

    public void doInTransaction(Consumer<Connection> action) {
        Object o = doInTransaction(connection -> {
            action.accept(connection);
            return null;
        });
        if (o != null) {
            log.trace("{}", o);
        }
    }


    public <T> T doInTransaction(Function<Connection, T> action) {
        return config.getJdbcTemplate().execute((ConnectionCallback<T>) connection -> {
            T result;
            boolean autoCommit = connection.getAutoCommit();
            try {
                if (autoCommit) {
                    connection.setAutoCommit(false);
                }
                result = action.apply(connection);
                connection.commit();
            } catch (Throwable e) {
                connection.rollback();
                throw Exceptions.sneakyThrow(e);
            } finally {
                if (autoCommit) {
                    connection.setAutoCommit(true);
                }
            }
            return result;
        });

    }

    public void doInJpaTransaction(Runnable action) {
        try {
            executeJpaAction(action);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    public void doInJdbcTransaction(Runnable action) {
        try {
            executeJdbcAction(action);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    private void executeJpaAction(Runnable action) throws SQLException {
        EntityTransaction transaction = config.getEntityManager().getTransaction();
        if (transaction.isActive()) {
            action.run();
            return;
        }
        transaction.begin();
        boolean rolledBack = false;
        try {
            action.run();
        } catch (Throwable e) {
            transaction.rollback();
            rolledBack = true;
            throw e;
        } finally {
            if (rolledBack) {
                transaction.rollback();
            } else {
                transaction.commit();
            }
        }
    }

    private void executeJdbcAction(Runnable action) throws SQLException {
        jdbcTransactionTemplate.execute(status -> {
            action.run();
            return null;
        });
    }

    private TransactionTemplate jdbcTransactionTemplate() {
        DataSource dataSource = Objects.requireNonNull(config.getJdbcTemplate().getDataSource());
        DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
        return new TransactionTemplate(manager);
    }


}
