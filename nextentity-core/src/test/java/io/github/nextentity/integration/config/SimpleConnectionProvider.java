package io.github.nextentity.integration.config;

import io.github.nextentity.jdbc.ConnectionProvider;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple connection provider that uses a single connection from the DataSource.
 * Used for integration tests with Testcontainers.
 *
 * @author HuangChengwei
 */
public class SimpleConnectionProvider implements ConnectionProvider {

    private final DataSource dataSource;
    private static final ThreadLocal<Transaction> TRANSACTION = new ThreadLocal<>();


    public SimpleConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <T> T execute(ConnectionCallback<T> action) throws SQLException {
        try (Transaction connection = getTransaction()) {
            return action.doInConnection(connection.connection);
        }
    }

    private Transaction getTransaction() throws SQLException {
        return getTransaction(dataSource::getConnection);
    }

    @Override
    public <T> T executeInTransaction(ConnectionCallback<T> action) throws SQLException {
        Transaction transaction = getTransaction().activeTransaction();
        try (transaction) {
            return action.doInConnection(transaction.connection);
        } catch (Exception e) {
            transaction.rollback = true;
            if (!transaction.connection.isClosed()) {
                transaction.connection.rollback();
            }
            throw e;
        }
    }

    static Transaction getTransaction(@NonNull ConnectionSupplier supplier) throws SQLException {
        Transaction transaction = TRANSACTION.get();
        if (transaction == null) {
            transaction = new Transaction(supplier.get());
            TRANSACTION.set(transaction);
        }
        return transaction.acquire();
    }

    public static Connection getOrBind(ConnectionSupplier o) throws SQLException {
        return getTransaction(o).connection;
    }

    private static class Transaction implements AutoCloseable {
        private final Connection connection;
        AtomicInteger acquireCount = new AtomicInteger();
        AtomicInteger activeCount = new AtomicInteger();
        boolean rollback;

        private Transaction(Connection connection) {
            this.connection = Objects.requireNonNull(connection);
        }

        Transaction acquire() {
            acquireCount.incrementAndGet();
            return this;
        }

        Transaction activeTransaction() throws SQLException {
            connection.setAutoCommit(false);
            activeCount.incrementAndGet();
            return this;
        }

        @Override
        public void close() throws SQLException {
            if (activeCount.decrementAndGet() == 0) {
                if (!rollback) {
                    connection.commit();
                }
                connection.setAutoCommit(true);
            }
            if (acquireCount.decrementAndGet() == 0) {
                connection.close();
                TRANSACTION.remove();
            }
        }
    }

}
