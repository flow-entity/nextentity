package io.github.nextentity.spring.integration;

import io.github.nextentity.spring.integration.db.DbConfig;
import io.github.nextentity.spring.integration.db.DbConfigs;
import io.github.nextentity.spring.integration.db.UserRepository;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class UserQueryProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return DbConfigs.CONFIGS.stream()
                .peek(dbConfig -> dbConfig.getEntityManager().clear())
                .flatMap(UserQueryProvider::getArguments)
                .map(Arguments::of);
    }

    private static Stream<UserRepository> getArguments(DbConfig config) {
        return config.repositories();
    }


}
