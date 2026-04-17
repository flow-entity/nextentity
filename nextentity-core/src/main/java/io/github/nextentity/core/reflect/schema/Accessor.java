package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.reflect.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Accessor {

    /// 获取此反射类型表示的Java类型。
    ///
    /// @return Java类
    Class<?> type();

    /// 获取属性名称。
    ///
    /// @return 名称
    String name();

    /// 获取此属性的 getter 方法。
    ///
    /// @return getter 方法，如果不可用则返回 null
    Method getter();

    /// 获取此属性的 setter 方法。
    ///
    /// @return setter 方法，如果不可用则返回 null
    Method setter();

    /// 获取此属性的字段。
    ///
    /// @return 字段，如果不可用则返回 null
    Field field();

    int ordinal();

    /// 从实体实例获取属性值。
    ///
    /// 如果 getter 方法可访问则使用它，否则直接访问字段。
    ///
    /// @param entity 实体实例
    /// @return 属性值
    /// @throws ReflectiveException 如果访问失败
    default Object get(Object entity) {
        try {
            Method getter = getter();
            if (getter != null && ReflectUtil.isAccessible(getter, entity)) {
                return getter.invoke(entity);
            } else {
                return ReflectUtil.getFieldValue(field(), entity);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectiveException(e);
        }
    }

    /// 在实体实例上设置属性值。
    ///
    /// 如果 setter 方法可访问则使用它，否则直接设置字段。
    ///
    /// @param entity 实体实例
    /// @param value  要设置的值
    /// @throws ReflectiveException 如果访问失败
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
            throw new ReflectiveException(e);
        }
    }
}
