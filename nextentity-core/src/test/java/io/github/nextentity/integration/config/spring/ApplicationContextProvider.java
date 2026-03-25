package io.github.nextentity.integration.config.spring;

import io.github.nextentity.integration.config.env.MysqlEnvironmentVariables;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.stream.Stream;

public class ApplicationContextProvider implements ArgumentsProvider {

    @Override
    @NonNull
    public Stream<? extends Arguments> provideArguments(@NonNull ParameterDeclarations parameters,
                                                        @NonNull ExtensionContext context) throws Exception {
        MysqlEnvironmentVariables mysql = new MysqlEnvironmentVariables();
        //spring.datasource.password
        ConfigurableApplicationContext mysqlCtx = SpringApplication.run(
                IntegrationTestApplication.class,
                "--spring.datasource.password=" + mysql.getPassword(),
                "--spring.datasource.url=" + mysql.getJdbcUrl(),
                "--spring.datasource.username=" + mysql.getUsername(),
                "--spring.datasource.driver-class-name=" + mysql.getDriverClassName()
        );
        return Stream.of(mysqlCtx).map(Arguments::of);
    }
}
