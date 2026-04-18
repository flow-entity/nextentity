package io.github.nextentity.integration.fast;

import io.github.nextentity.integration.config.IntegrationTestContext;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.util.stream.Stream;

///
/// Fast arguments provider for parameterized tests with H2 only.
/// Use for quick validation during development - no containers, fast startup.
///
/// @author HuangChengwei
public class FastIntegrationTestProvider implements ArgumentsProvider {

    private static final ThreadLocal<IntegrationTestContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public @NonNull Stream<? extends Arguments> provideArguments(@NonNull ParameterDeclarations parameters,
                                                                 @NonNull ExtensionContext context) {
        return FastApplicationContexts.contexts()
                .stream()
                .flatMap(ctx -> ctx.getBeansOfType(IntegrationTestContext.class).values().stream())
                .peek(CONTEXT_THREAD_LOCAL::set)
                .map(Arguments::of);
    }

    public static IntegrationTestContext getEntityManagerContext() {
        return CONTEXT_THREAD_LOCAL.get();
    }
}