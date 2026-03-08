package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ValueConvertor;

public interface Arguments {

    Object get(int index, ValueConvertor<?, ?> convertor);

    Object next(ValueConvertor<?, ?> convertor);

}
