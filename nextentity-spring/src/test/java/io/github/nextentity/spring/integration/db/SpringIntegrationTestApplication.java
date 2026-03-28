package io.github.nextentity.spring.integration.db;

import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Spring Boot application for integration tests.
 * Provides JDBC and JPA based test contexts for each database.
 *
 * @author HuangChengwei
 */
@EntityScan("io.github.nextentity.spring.integration.entity")
@SpringBootApplication
public class SpringIntegrationTestApplication {

    @Bean(name = "jdbcUserRepository")
    public UserRepository jdbcUserRepository(JdbcTemplate jdbcTemplate, DatabaseEnvironment env) {
        return new UserRepository(jdbcTemplate, env.get().getName() + "-jdbc");
    }

    @Bean("jpaUserRepository")
    public UserRepository jpaUserRepository(JdbcTemplate jdbcTemplate, EntityManager entityManager, DatabaseEnvironment env) {
        return new UserRepository(entityManager, jdbcTemplate, env.get().getName() + "-jpa");
    }

    @Bean
    public DatabaseEnvironment databaseEnvironment() {
        return new DatabaseEnvironment();
    }

}