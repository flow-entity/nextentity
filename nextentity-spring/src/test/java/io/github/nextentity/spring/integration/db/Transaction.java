package io.github.nextentity.spring.integration.db;

import io.github.nextentity.core.exception.UncheckedSQLException;
import io.github.nextentity.core.util.Exceptions;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author HuangChengwei
 * @since 2024-04-10 16:19
 */
public class Transaction {
    private static final Logger log = LoggerFactory.getLogger(Transaction.class);
    protected DbConfig config;

    public Transaction() {
    }

    public Transaction(DbConfig config) {
        this.config = config;
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

    public void doInTransaction(Runnable action) {
        try {
            executeAction(action);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    private void executeAction(Runnable action) throws SQLException {
        EntityTransaction transaction = config.getEntityManager().getTransaction();
        boolean rollback = false;
        JdbcTemplate provider = config.getJdbcTemplate();
        provider.execute((Connection connection) -> {
            connection.setAutoCommit(false);
            return null;
        });
        transaction.begin();
        try {
            action.run();
        } catch (Throwable throwable) {
            rollback = true;
            provider.execute((Connection connection) -> {
                connection.rollback();
                return null;
            });
            transaction.rollback();
            throw throwable;
        } finally {
            if (!rollback) {
                provider.execute((Connection connection) -> {
                    connection.commit();
                    return null;
                });
                transaction.commit();
            }
        }
    }


}
