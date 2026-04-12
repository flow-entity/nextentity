package io.github.nextentity.core;

import java.util.function.Supplier;

public interface TransactionOperations {

    /**
     * 在事务中执行给定的操作
     *
     * @param operation 要执行的操作
     */
    default void executeInTransaction(Runnable operation) {
        executeInTransaction(() -> {
            operation.run();
            return null;
        });
    }

    /**
     * 在事务中执行给定的操作，并返回结果
     *
     * @param operation 要执行的操作
     * @param <T>       结果类型
     * @return 操作结果
     */
    <T> T executeInTransaction(Supplier<T> operation);

}
