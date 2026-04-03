package io.github.nextentity.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Supplier;

///
/// 默认的 JPA 事务模板实现，提供基本的事务执行功能。
/// 该实现确保在给定的实体管理器中正确地开始、提交或回滚事务。
///
/// @author HuangChengwei
/// @since 2.1
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
