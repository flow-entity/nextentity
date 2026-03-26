package io.github.nextentity.integration.config.spring;

import io.github.nextentity.integration.config.env.DatabaseEnvironmentVariables;
import io.github.nextentity.integration.config.env.Env;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.stream.Stream;

public class ApplicationContextProvider implements ArgumentsProvider {

    private static final List<ConfigurableApplicationContext> CONTEXTS = Env.dbs().stream()
            .map(ApplicationContextProvider::getApplicationContext)
            .toList();

    @NonNull
    @Override
    public Stream<Arguments> provideArguments(@NonNull ParameterDeclarations parameters, @NonNull ExtensionContext context) {
        return contexts().stream().map(Arguments::of);
    }

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
        return context;
    }
}
