package io.github.nextentity.spring.integration.db;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

/**
 * @author HuangChengwei
 */
public interface DbConfigProvider {

    default DbConfig getConfig() {
        return new DbConfig(
                getDataSource(),
                getEntityManagerFactory().createEntityManager(),
                setPidNullSql(),
                name());
    }

    DataSource getDataSource();

    EntityManagerFactory getEntityManagerFactory();

    String setPidNullSql();

    String name();

}
