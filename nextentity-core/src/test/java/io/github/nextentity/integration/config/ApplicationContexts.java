package io.github.nextentity.integration.config;

import io.github.nextentity.integration.config.env.DatabaseEnvironmentVariables;
import io.github.nextentity.integration.config.env.Env;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

public class ApplicationContexts {

    private static final List<ConfigurableApplicationContext> CONTEXTS = Env.dbs().stream()
            .map(ApplicationContexts::getApplicationContext)
            .toList();

    public static List<ConfigurableApplicationContext> contexts() {
        return CONTEXTS;
    }

    private static @NonNull ConfigurableApplicationContext getApplicationContext(DatabaseEnvironmentVariables dbEnv) {
        // spring.datasource.password
        ConfigurableApplicationContext context = SpringApplication.run(
                IntegrationTestApplication.class,
                "--spring.datasource.password=" + dbEnv.getPassword(),
                "--spring.datasource.url=" + dbEnv.getJdbcUrl(),
                "--spring.datasource.username=" + dbEnv.getUsername(),
                "--spring.datasource.driver-class-name=" + dbEnv.getDriverClassName()
        );

        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        defaultListableBeanFactory.registerSingleton("databaseEnvironmentVariables", dbEnv);

        // Initialize database schema and test data once per context
        initializeDatabase(context);

        return context;
    }

    private static void initializeDatabase(ConfigurableApplicationContext context) {
        // Get all IntegrationTestContext beans and call reset() to initialize database
        context.getBeansOfType(IntegrationTestContext.class).values()
                .forEach(IntegrationTestContext::reset);
    }
}
