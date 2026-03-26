package io.github.nextentity.integration.config.spring;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.env.DatabaseEnvironmentVariables;
import io.github.nextentity.jdbc.ConnectionProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationContextTest {

    @ParameterizedTest
    @ArgumentsSource(ApplicationContextProvider.class)
    void test(ApplicationContext context) {
        printBean(context, DatabaseEnvironmentVariables.class);
        printBean(context, ConnectionProvider.class);
        printBean(context, IntegrationTestApplication.DatabaseInitializer.class);

        for (IntegrationTestContext value : context.getBeansOfType(IntegrationTestContext.class).values()) {
            System.out.println(value);
        }
    }

    private static void printBean(ApplicationContext context, Class<?> requiredType) {
        var bean = context.getBean(requiredType);
        System.out.println(bean);
    }

}
