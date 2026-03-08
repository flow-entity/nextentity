package io.github.nextentity.jdbc;

public record NullParameter(Class<?> type) implements TypedParameter {
    @Override
    public String toString() {
        return "(" + type.getSimpleName() + ")null";
    }

    @Override
    public Object value() {
        return null;
    }
}
