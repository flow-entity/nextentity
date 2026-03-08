package io.github.nextentity.core.meta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author HuangChengwei
 * @since 1.0.0
 */
public class OrdinalOfEnumType implements DatabaseType {
    private final Class<?> databaseType;
    private final Object[] values;

    public OrdinalOfEnumType(Class<?> attributeType) {
        this.databaseType = Integer.class;
        try {
            Method method = attributeType.getDeclaredMethod("values");
            this.values = (Object[]) method.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> databaseType() {
        return databaseType;
    }

    @Override
    public Object toDatabaseType(Object value) {
        return value == null ? null : ((Enum<?>) value).ordinal();
    }

    @Override
    public Object toAttributeType(Object value) {
        return value instanceof Integer index ? values[index] : value;
    }
}
