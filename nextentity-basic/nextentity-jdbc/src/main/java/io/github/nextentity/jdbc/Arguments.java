package io.github.nextentity.jdbc;

public interface Arguments {

    Object get(int index, Class<?> type);

    Object next(Class<?> type);

}
