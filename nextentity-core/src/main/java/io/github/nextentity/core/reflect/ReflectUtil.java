package io.github.nextentity.core.reflect;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.util.Exceptions;
import org.jspecify.annotations.NonNull;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectUtil {
    private static final Map<Class<?>, Object> SINGLE_ENUM_MAP = new ConcurrentHashMap<>();

    public static Field getDeclaredField(@NonNull Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return getDeclaredField(superclass, name);
            }
        }
        return null;
    }

    public static <T> void copyTargetNullFields(T src, T target, Class<T> type) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                Method reader = descriptor.getReadMethod();
                Method writer = descriptor.getWriteMethod();
                if (reader != null && writer != null) {
                    Object tv = reader.invoke(target);
                    if (tv != null) {
                        continue;
                    }
                    Object sv = reader.invoke(src);
                    if (sv != null) {
                        writer.invoke(target, sv);
                    }
                }
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        return InvocationHandler.invokeDefault(proxy, method, args);
    }

    public static Object getFieldValue(Field field, Object instance) throws IllegalAccessException {
        setAccessible(field, instance);
        return field.get(instance);
    }

    public static void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
        setAccessible(field, instance);
        field.set(instance, value);
    }

    private static void setAccessible(AccessibleObject accessible, Object instance) {
        if (!isAccessible(accessible, instance)) {
            accessible.setAccessible(true);
        }
    }

    public static boolean isAccessible(AccessibleObject accessibleObject, Object instance) {
        return accessibleObject.canAccess(instance);
    }

    @NonNull
    public static Object newProxyInstance(@NonNull Class<?> resultType, Map<Method, Object> map) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?>[] interfaces = {resultType};
        return Proxy.newProxyInstance(classLoader, interfaces, new InstanceInvocationHandler(resultType, map));
    }

    public static void typeCheck(Object value, Class<?> type) {
        if (value == null) {
            if (type.isPrimitive()) {
                throw new ReflectiveException("primitive type value can not be null");
            }
        } else if (!type.isInstance(value)) {
            if (type.isPrimitive()) {
                type = PrimitiveTypes.getWrapper(type);
            }
            if (!type.isInstance(value)) {
                throw new ReflectiveException(value.getClass() + "[" + value + "] can not cast to " + type);
            }
        }
    }

    public static Object getEnum(Class<?> cls, int ordinal) {
        if (!cls.isEnum()) {
            throw new IllegalArgumentException();
        }
        Object array = SINGLE_ENUM_MAP.computeIfAbsent(cls, k -> {
            try {
                Method method = cls.getMethod("values");
                setAccessible(method, null);
                return method.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw Exceptions.sneakyThrow(e);
            }
        });
        return Array.get(array, ordinal);
    }

    public static Object getEnum(Class<?> cls, String name) {
        if (!cls.isEnum()) {
            throw new IllegalArgumentException();
        }
        try {
            Method method = cls.getMethod("valueOf", String.class);
            setAccessible(method, null);
            return method.invoke(null, name);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw Exceptions.sneakyThrow(e);
        }
    }

}
