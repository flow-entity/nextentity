package io.github.nextentity.spring.integration.db;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class UserQueryProvider implements ArgumentsProvider {

    @NonNull
    @Override
    public Stream<? extends Arguments> provideArguments(@NonNull ParameterDeclarations parameters, @NonNull ExtensionContext context) {
        String property = System.getProperty("dbc");
        List<String> dbs = new  ArrayList<>();
        if (property == null || property.isBlank()) {
            DB annotation = context.getRequiredTestMethod().getAnnotation(DB.class);
            if(annotation != null) {
                Collections.addAll(dbs, annotation.value());
            }
        } else {
            Collections.addAll(dbs, property.split(","));
        }
        return ApplicationContexts.contexts(dbs.toArray(new String[0])).stream()
                .flatMap(ctx -> ctx.getBeansOfType(UserRepository.class).values().stream())
                .filter(repo -> {
                    DB annotation = context.getRequiredTestMethod().getAnnotation(DB.class);
                    if(annotation != null&& annotation.type().length > 0) {
                        String name = repo.getName();
                        for (String s : annotation.type()) {
                            if (name.contains(s)) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return true;
                })
                .map(Arguments::of);
    }
}