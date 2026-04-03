package io.github.nextentity.spring.integration.db;

import io.github.nextentity.spring.DefaultNextEntityFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

/// 用于集成测试的 Spring Boot 应用程序。
/// 为每个数据库提供基于 JDBC 和 JPA 的测试上下文。
///
/// @author HuangChengwei
@EntityScan("io.github.nextentity.spring.integration.entity")
@SpringBootApplication
public class SpringIntegrationTestApplication {

    @Bean(name = "jdbcUserRepository")
    public UserRepository jdbcUserRepository(JdbcTemplate jdbcTemplate, DatabaseEnvironment env) {
        UserRepository repository = new UserRepository();
        String name = env.get().getName() + "-jdbc";
        repository.setName(name);
        repository.setFactory(DefaultNextEntityFactory.jdbc(jdbcTemplate));
        return repository;
    }

    @Bean("jpaUserRepository")
    public UserRepository jpaUserRepository(JdbcTemplate jdbcTemplate, EntityManager entityManager, DatabaseEnvironment env) {
        UserRepository repository = new UserRepository();
        String name = env.get().getName() + "-jpa";
        repository.setName(name);
        repository.setFactory(DefaultNextEntityFactory.jpa(entityManager, jdbcTemplate));
        return repository;
    }

    @Bean
    public DatabaseEnvironment databaseEnvironment() {
        return new DatabaseEnvironment();
    }

}
