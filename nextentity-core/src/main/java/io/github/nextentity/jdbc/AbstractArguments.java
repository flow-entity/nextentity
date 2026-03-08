package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ValueConverter;

public abstract class AbstractArguments implements Arguments {

    private int index;

    @Override
    public Object next(ValueConverter<?, ?> convertor) {
        return get(index++, convertor);
    }

}