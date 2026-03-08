package io.github.nextentity.jpa;

import io.github.nextentity.core.meta.ValueConverter;
import io.github.nextentity.jdbc.AbstractArguments;

/**
 * @author HuangChengwei
 * @since 1.0.0
 */
public class JpaArguments extends AbstractArguments {
    private final Object[] objects;

    public JpaArguments(Object[] objects) {
        this.objects = objects;
    }

    @Override
    public Object get(int index, ValueConverter<?, ?> convertor) {
        return objects[index];
    }

}
