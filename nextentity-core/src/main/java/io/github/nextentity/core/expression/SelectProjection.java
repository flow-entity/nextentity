package io.github.nextentity.core.expression;

public record SelectProjection(Class<?> type, boolean distinct) implements Selected {
}
