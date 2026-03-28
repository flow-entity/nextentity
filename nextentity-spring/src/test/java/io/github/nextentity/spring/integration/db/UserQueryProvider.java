package io.github.nextentity.spring.integration.db;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.util.stream.Stream;

public class UserQueryProvider implements ArgumentsProvider {

    @NonNull
    @Override
    public Stream<? extends Arguments> provideArguments(@NonNull ParameterDeclarations parameters, @NonNull ExtensionContext context) {
        return ApplicationContexts.contexts().stream()
                .flatMap(ctx -> ctx.getBeansOfType(UserRepository.class).values().stream())
                .map(Arguments::of);
    }
}