package io.github.nextentity.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Supplier;

public class DefaultTransactionTemplate implements JpaTransactionTemplate {

    private static final JpaTransactionTemplate JPA_TRANSACTION_TEMPLATE = new DefaultTransactionTemplate();

    public static JpaTransactionTemplate of() {
        return JPA_TRANSACTION_TEMPLATE;
    }

    @Override
    public <T> T executeInTransaction(EntityManager entityManager, Supplier<T> command) {
        EntityTransaction transaction = entityManager.getTransaction();
        if (transaction.isActive()) {
            return command.get();
        }
        transaction.begin();
        try {
            T result = command.get();
            transaction.commit();
            return result;
        } catch (Throwable e) {
            transaction.rollback();
            throw e;
        }
    }

}
