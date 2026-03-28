package io.github.nextentity.spring.integration.db;

import io.github.nextentity.spring.integration.db.env.DatabaseEnvironmentVariables;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * Manages Spring Boot application contexts for integration tests.
 * Creates and holds contexts for each database (MySQL, PostgreSQL).
 *
 * @author HuangChengwei
 */
public class ApplicationContexts {

    private static final List<ConfigurableApplicationContext> CONTEXTS = DatabaseEnvironmentVariables.DBS.stream()
            .map(ApplicationContexts::getApplicationContext)
            .toList();

    public static List<ConfigurableApplicationContext> contexts() {
        return CONTEXTS;
    }

    private static @NonNull ConfigurableApplicationContext getApplicationContext(DatabaseEnvironmentVariables dbEnv) {
        return SpringApplication.run(
                SpringIntegrationTestApplication.class,
                "--application.env.name=" + dbEnv.getName(),
                "--spring.datasource.url=" + dbEnv.getJdbcUrl(),
                "--spring.datasource.username=" + dbEnv.getUsername(),
                "--spring.datasource.password=" + dbEnv.getPassword(),
                "--spring.datasource.driver-class-name=" + dbEnv.getDriverClassName(),
                "--spring.jpa.hibernate.ddl-auto=update",
                "--spring.jpa.show-sql=false",
                "--spring.jpa.properties.hibernate.format_sql=true"
        );
    }
}