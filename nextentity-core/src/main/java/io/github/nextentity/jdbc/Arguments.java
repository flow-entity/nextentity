package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ValueConverter;

public interface Arguments {

    Object get(int index, ValueConverter<?, ?> convertor);

    Object next(ValueConverter<?, ?> convertor);

}
