package io.github.nextentity.integration.config;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

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
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        // Reset test data before each test method to ensure clean state
        for (DbConfig config : DbConfigs.CONFIGS) {
            config.resetTestData();
        }
        return DbConfigs.CONFIGS.stream().map(Arguments::of);
    }
}
