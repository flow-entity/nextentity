package io.github.nextentity.core.reflect;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.util.Exceptions;
import io.github.nextentity.core.util.NullableConcurrentMap;
import org.jspecify.annotations.NonNull;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// 反射工具类，提供各种反射操作的便捷方法。
///
/// @author HuangChengwei
/// @since 1.0.0
public class ReflectUtil {
    private static final Map<Class<?>, Object> SINGLE_ENUM_MAP = new ConcurrentHashMap<>();

    /// 获取指定类及其父类中声明的字段。
    ///
    /// @param clazz 要搜索的类
    /// @param name  字段名称
    /// @return 找到的字段，如果没有找到则返回null
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

    /// 将源对象中非空字段复制到目标对象的空字段中。
    ///
    /// @param src    源对象
    /// @param target 目标对象
    /// @param type   对象类型
    /// @param <T>    对象类型
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

    /// 调用接口的默认方法。
    ///
    /// @param proxy  代理对象
    /// @param method 方法
    /// @param args   方法参数
    /// @return 方法调用结果
    /// @throws Throwable 方法调用可能抛出的异常
    public static Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        return InvocationHandler.invokeDefault(proxy, method, args);
    }

    /// 获取字段的值。
    ///
    /// @param field    字段
    /// @param instance 对象实例
    /// @return 字段值
    /// @throws IllegalAccessException 当字段无法访问时抛出
    public static Object getFieldValue(Field field, Object instance) throws IllegalAccessException {
        setAccessible(field, instance);
        return field.get(instance);
    }

    /// 设置字段的值。
    ///
    /// @param field    字段
    /// @param instance 对象实例
    /// @param value    要设置的值
    /// @throws IllegalAccessException 当字段无法访问时抛出
    public static void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
        setAccessible(field, instance);
        field.set(instance, value);
    }

    /// 设置可访问对象的可访问性。
    ///
    /// @param accessible 可访问对象
    /// @param instance   对象实例
    private static void setAccessible(AccessibleObject accessible, Object instance) {
        if (!isAccessible(accessible, instance)) {
            accessible.setAccessible(true);
        }
    }

    /// 检查可访问对象是否可访问。
    ///
    /// @param accessibleObject 可访问对象
    /// @param instance         对象实例
    /// @return 如果可访问返回true，否则返回false
    public static boolean isAccessible(AccessibleObject accessibleObject, Object instance) {
        return accessibleObject.canAccess(instance);
    }

    /// 创建代理实例。
    ///
    /// @param resultType 代理接口类型
    /// @param map        方法到实现对象的映射
    /// @return 代理实例
    @NonNull
    public static Object newProxyInstance(@NonNull Class<?> resultType, NullableConcurrentMap<Method, Object> map) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?>[] interfaces = {resultType};
        return Proxy.newProxyInstance(classLoader, interfaces, new InstanceInvocationHandler(resultType, map));
    }

    /// 类型检查。
    ///
    /// @param value 要检查的值
    /// @param type  期望的类型
    /// @throws ReflectiveException 当类型不匹配时抛出
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

    /// 根据序数获取枚举值。
    ///
    /// @param cls     枚举类
    /// @param ordinal 枚举值的序数
    /// @return 枚举值
    /// @throws IllegalArgumentException 当不是枚举类型时抛出
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

    /// 根据名称获取枚举值。
    ///
    /// @param cls  枚举类
    /// @param name 枚举值的名称
    /// @return 枚举值
    /// @throws IllegalArgumentException 当不是枚举类型时抛出
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