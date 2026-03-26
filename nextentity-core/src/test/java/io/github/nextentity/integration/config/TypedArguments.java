package io.github.nextentity.integration.config;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.params.provider.Arguments;

public class TypedArguments<T> implements Arguments {

    private final T[] arguments;

    public TypedArguments(T[] arguments) {
        this.arguments = arguments;
    }

    @SafeVarargs
    public static <T> TypedArguments<T> of(T... arguments) {
        return new TypedArguments<>(arguments);
    }

    @Override
    public Object @NonNull [] get() {
        return arguments;
    }


}
