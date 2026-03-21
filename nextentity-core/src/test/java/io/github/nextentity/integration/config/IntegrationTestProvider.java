package io.github.nextentity.integration.config;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.util.stream.Stream;

/**
 * Arguments provider for parameterized integration tests.
 * Provides DbConfig instances for MySQL and PostgreSQL.
 * Resets test data before each test method execution.
 *
 * @author HuangChengwei
 */
public class IntegrationTestProvider implements ArgumentsProvider {

    private static final Mysql MYSQL = new Mysql();
    private static final Postgresql POSTGRESQL = new Postgresql();

    @Override
    public @NonNull Stream<? extends Arguments> provideArguments(@NonNull ParameterDeclarations parameters,
                                                                 @NonNull ExtensionContext context) {
        return Stream.of(MYSQL, POSTGRESQL)
                .flatMap(it -> it.getConfigs().stream())
                .map(arguments -> Arguments.of(arguments.reset()));
    }
}
