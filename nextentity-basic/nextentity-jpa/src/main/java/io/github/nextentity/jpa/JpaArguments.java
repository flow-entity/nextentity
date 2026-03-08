package io.github.nextentity.jpa;

import io.github.nextentity.core.meta.ValueConvertor;
import io.github.nextentity.jdbc.AbstractArguments;

/**
 * @author HuangChengwei
 * @since 2024/4/17 下午5:17
 */
public class JpaArguments extends AbstractArguments {
    private final Object[] objects;

    public JpaArguments(Object[] objects) {
        this.objects = objects;
    }

    @Override
    public Object get(int index, ValueConvertor<?, ?> convertor) {
        return objects[index];
    }

}
