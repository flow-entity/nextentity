package io.github.nextentity.jpa;

import jakarta.persistence.EntityManager;

import java.util.function.Supplier;

public interface JpaTransactionTemplate {

    <T> T executeInTransaction(EntityManager entityManager, Supplier<T> action);


}
