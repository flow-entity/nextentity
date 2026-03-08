package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ValueConvertor;

public abstract class AbstractArguments implements Arguments {

    private int index;

    @Override
    public Object next(ValueConvertor<?, ?> convertor) {
        return get(index++, convertor);
    }

}