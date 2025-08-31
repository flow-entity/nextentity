package io.github.nextentity.jdbc;

public abstract class AbstractArguments implements Arguments {

    private int index;

    @Override
    public Object next(Class<?> type) {
        return get(index++, type);
    }

}