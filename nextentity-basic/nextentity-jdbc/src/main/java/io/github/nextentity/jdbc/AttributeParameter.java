package io.github.nextentity.jdbc;

public record AttributeParameter(Object value, Class<?> type) implements TypedParameter {
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
