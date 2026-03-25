package io.github.nextentity.integration.config.spring;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationContextTest {

    @ParameterizedTest
    @ArgumentsSource(ApplicationContextProvider.class)
    void test(ApplicationContext context) {
        System.out.println(context);
    }

}
