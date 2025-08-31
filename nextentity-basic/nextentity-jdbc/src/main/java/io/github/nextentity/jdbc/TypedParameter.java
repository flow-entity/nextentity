package io.github.nextentity.jdbc;

public interface TypedParameter {
    Class<?> type();

    Object value();
}
