package io.github.nextentity.integration.fast;

import io.github.nextentity.integration.config.IntegrationTestApplication;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.env.H2EnvironmentVariables;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

///
/// Fast application contexts for quick testing with H2 in-memory database.
/// Single database, no containers - suitable for rapid validation during development.
///
/// @author HuangChengwei
public class FastApplicationContexts {

    private static final ConfigurableApplicationContext CONTEXT = createContext();

    private static @NonNull ConfigurableApplicationContext createContext() {
        H2EnvironmentVariables h2Env = new H2EnvironmentVariables();
        ConfigurableApplicationContext context = SpringApplication.run(
                IntegrationTestApplication.class,
                "--spring.datasource.password=" + h2Env.getPassword(),
                "--spring.datasource.url=" + h2Env.getJdbcUrl(),
                "--spring.datasource.username=" + h2Env.getUsername(),
                "--spring.datasource.driver-class-name=" + h2Env.getDriverClassName()
        );

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        beanFactory.registerSingleton("databaseEnvironmentVariables", h2Env);

        initializeDatabase(context);

        return context;
    }

    private static void initializeDatabase(ConfigurableApplicationContext context) {
        context.getBeansOfType(IntegrationTestContext.class).values()
                .forEach(IntegrationTestContext::reset);
    }

    public static List<ConfigurableApplicationContext> contexts() {
        return List.of(CONTEXT);
    }

    public static ConfigurableApplicationContext context() {
        return CONTEXT;
    }
}