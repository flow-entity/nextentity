package io.github.nextentity.core.reflect.schema;


import io.github.nextentity.core.exception.BeanReflectiveException;
import io.github.nextentity.core.reflect.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author HuangChengwei
 * @since 2024/4/18 下午12:55
 * <p>
 */
public non-sealed interface Attribute extends ReflectType {

    String name();

    Method getter();

    Method setter();

    Field field();

    Schema declareBy();

    int ordinal();

    default int deep() {
        if (!(declareBy() instanceof Attribute)) {
            return 1;
        }
        return ((Attribute) declareBy()).deep() + 1;
    }

    default Object get(Object entity) {
        try {
            Method getter = getter();
            if (getter != null && ReflectUtil.isAccessible(getter, entity)) {
                return getter.invoke(entity);
            } else {
                return ReflectUtil.getFieldValue(field(), entity);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanReflectiveException(e);
        }
    }

    default void set(Object entity, Object value) {
        try {
            Method setter = setter();
            if (setter != null && ReflectUtil.isAccessible(setter, entity)) {
                ReflectUtil.typeCheck(value, setter.getParameterTypes()[0]);
                setter.invoke(entity, value);
            } else {
                ReflectUtil.typeCheck(value, field().getType());
                ReflectUtil.setFieldValue(field(), entity, value);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanReflectiveException(e);
        }
    }

}
