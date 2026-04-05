package io.github.nextentity.jpa;

import io.github.nextentity.core.meta.ValueConverter;
import io.github.nextentity.jdbc.AbstractArguments;

///
/// JPA 参数封装类，用于封装传递给查询的参数值。
/// 该类继承自 AbstractArguments，实现了 JPA 查询的参数处理逻辑。
///
/// @author HuangChengwei
/// @since 2.0.0
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
