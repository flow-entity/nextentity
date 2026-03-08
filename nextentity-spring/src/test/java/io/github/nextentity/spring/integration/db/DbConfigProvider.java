package io.github.nextentity.spring.integration.db;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

/**
 * @author HuangChengwei
 * @since 2024-04-10 10:45
 */
public interface DbConfigProvider {

    default DbConfig getConfig() {
        return new DbConfig(
                getDataSource(),
                getEntityManagerFactory().createEntityManager(),
                setPidNullSql());
    }

    DataSource getDataSource();

    EntityManagerFactory getEntityManagerFactory();

    String setPidNullSql();

    String name();

}
