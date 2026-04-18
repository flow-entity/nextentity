package io.github.nextentity.integration.config;

import io.github.nextentity.integration.config.env.DatabaseEnvironmentVariables;
import io.github.nextentity.integration.config.env.Env;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContexts {

    private static final Map<String, ConfigurableApplicationContext> CACHE = new ConcurrentHashMap<>();

    public static List<ConfigurableApplicationContext> contexts() {
        return Env.getSelectedDbTypes().stream()
                .map(ApplicationContexts::getOrCreateContext)
                .toList();
    }

    private static @NonNull ConfigurableApplicationContext getOrCreateContext(String dbType) {
        return CACHE.computeIfAbsent(dbType, k -> {
            DatabaseEnvironmentVariables dbEnv = Env.dbs().stream()
                    .filter(e -> e.name().equals(k))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Database not found: " + k));
            return createApplicationContext(dbEnv);
        });
    }

    private static @NonNull ConfigurableApplicationContext createApplicationContext(DatabaseEnvironmentVariables dbEnv) {
        ConfigurableApplicationContext context = SpringApplication.run(
                IntegrationTestApplication.class,
                "--spring.datasource.password=" + dbEnv.getPassword(),
                "--spring.datasource.url=" + dbEnv.getJdbcUrl(),
                "--spring.datasource.username=" + dbEnv.getUsername(),
                "--spring.datasource.driver-class-name=" + dbEnv.getDriverClassName()
        );

        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        defaultListableBeanFactory.registerSingleton("databaseEnvironmentVariables", dbEnv);

        initializeDatabase(context);

        return context;
    }

    private static void initializeDatabase(ConfigurableApplicationContext context) {
        context.getBeansOfType(IntegrationTestContext.class).values()
                .forEach(IntegrationTestContext::reset);
    }
}