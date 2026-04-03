package io.github.nextentity.jpa;

import jakarta.persistence.EntityManager;

import java.util.function.Supplier;

///
/// JPA 事务模板接口，定义在 JPA 实体管理器上下文中执行事务操作的方法。
/// 该接口提供了一个统一的方式来在事务中执行数据库操作。
///
/// @author HuangChengwei
/// @since 2.1
public interface JpaTransactionTemplate {

    <T> T executeInTransaction(EntityManager entityManager, Supplier<T> action);


}
