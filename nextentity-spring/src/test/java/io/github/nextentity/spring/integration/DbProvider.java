package io.github.nextentity.spring.integration;

import io.github.nextentity.spring.integration.db.DbConfigs;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

/**
 * @author HuangChengwei
 */
public class DbProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return DbConfigs.CONFIGS.stream()
                .map(Arguments::of);
    }
}
