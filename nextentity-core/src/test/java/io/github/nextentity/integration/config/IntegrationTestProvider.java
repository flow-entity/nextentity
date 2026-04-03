package io.github.nextentity.integration.config;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.util.stream.Stream;

///
 /// Arguments provider for parameterized integration tests.
 /// Provides DbConfig instances for MySQL and PostgreSQL.
 /// <p>
 /// Note: 测试 data reset is no longer automatic. 测试s that modify data
 /// should call {@link Integration测试Context#reset()} in {@code @AfterEach}
 /// or at the end of each test 方法.
 /// 
 /// @author HuangChengwei
public class IntegrationTestProvider implements ArgumentsProvider {

    private static final ThreadLocal<IntegrationTestContext> INTEGRATION_TEST_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public @NonNull Stream<? extends Arguments> provideArguments(@NonNull ParameterDeclarations parameters,
                                                                 @NonNull ExtensionContext context) {

        return ApplicationContexts.contexts()
                .stream()
                .flatMap(ctx -> ctx.getBeansOfType(IntegrationTestContext.class).values().stream())
                .peek(INTEGRATION_TEST_CONTEXT_THREAD_LOCAL::set)
                .map(Arguments::of);
    }

    public static IntegrationTestContext getEntityManagerContext() {
        return INTEGRATION_TEST_CONTEXT_THREAD_LOCAL.get();
    }
}
