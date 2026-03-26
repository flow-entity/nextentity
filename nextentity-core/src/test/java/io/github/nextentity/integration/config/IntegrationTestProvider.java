package io.github.nextentity.integration.config;

import io.github.nextentity.integration.config.spring.ApplicationContextProvider;
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

    @Override
    public @NonNull Stream<? extends Arguments> provideArguments(@NonNull ParameterDeclarations parameters,
                                                                 @NonNull ExtensionContext context) {

        return ApplicationContextProvider.contexts()
                .stream()
                .flatMap(ctx -> ctx.getBeansOfType(IntegrationTestContext.class).values().stream())
                .map(arguments -> Arguments.of(arguments.reset()));
    }
}
