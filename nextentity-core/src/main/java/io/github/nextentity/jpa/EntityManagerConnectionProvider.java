package io.github.nextentity.jpa;

import io.github.nextentity.jdbc.ConnectionProvider;
import jakarta.persistence.EntityManager;

import java.sql.SQLException;

///
/// 基于 EntityManager 的 ConnectionProvider 实现
///
/// 通过 EntityManager.unwrap() 获取 JDBC Connection，
/// 用于在 JPA 环境下执行原生 JDBC 查询。
///
/// @author HuangChengwei
/// @since 2.0.0
public class EntityManagerConnectionProvider implements ConnectionProvider {

    private final EntityManager entityManager;

    /// 构造 EntityManagerConnectionProvider
    ///
    /// @param entityManager JPA Entity Manager 实例
    public EntityManagerConnectionProvider(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <T> T execute(ConnectionCallback<T> action) throws SQLException {
        return entityManager.callWithConnection(action::doInConnection);
    }
}