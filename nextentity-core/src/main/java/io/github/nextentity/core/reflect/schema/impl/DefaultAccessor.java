package io.github.nextentity.core.reflect.schema.impl;

import io.github.nextentity.core.PathReference;
import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.reflect.schema.Accessor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public record DefaultAccessor(
        Class<?> type,
        String name,
        Method getter,
        Method setter,
        Field field,
        int ordinal
) implements Accessor {

    private static final Map<Class<?>, List<DefaultAccessor>> cache = new ConcurrentHashMap<>();

    public static List<DefaultAccessor> of(Class<?> type) {
        if (isSimpleType(type)) {
            return List.of();
        }
        return cache.computeIfAbsent(type, DefaultAccessor::createAccessors);
    }

    private static List<DefaultAccessor> createAccessors(Class<?> type) {
        if (type.isRecord()) {
            return createRecordAccessors(type);
        }
        Map<String, PropertyDescriptor> descriptorMap = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String fieldName = descriptor.getName();
                descriptorMap.put(fieldName, descriptor);
            }
        } catch (IntrospectionException e) {
            throw new ReflectiveException(e);
        }

        // 获取所有声明字段（包括父类）
        Map<String, Field> fieldMap = getDeclaredFields(type);

        // 创建属性列表
        List<DefaultAccessor> result = new ArrayList<>();

        int ordinal = 0;
        // 先处理有对应字段的属性
        for (Field field : fieldMap.values()) {
            PropertyDescriptor descriptor = descriptorMap.remove(field.getName());
            String name = descriptor == null ? field.getName() : descriptor.getName();
            if ("class".equals(name)) {
                continue;
            }
            DefaultAccessor attribute = new DefaultAccessor(
                    descriptor == null ? field.getType() : descriptor.getPropertyType(),
                    name,
                    descriptor == null ? null : descriptor.getReadMethod(),
                    descriptor == null ? null : descriptor.getWriteMethod(),
                    field,
                    ordinal++
            );
            result.add(attribute);
        }

        // 再处理只有 getter/setter 的属性（如接口方法）
        for (PropertyDescriptor descriptor : descriptorMap.values()) {
            String name = descriptor.getName();
            if ("class".equals(name)) {
                continue;
            }
            DefaultAccessor attribute = new DefaultAccessor(
                    descriptor.getPropertyType(),
                    name,
                    descriptor.getReadMethod(),
                    descriptor.getWriteMethod(),
                    null,
                    ordinal++
            );
            result.add(attribute);
        }

        return List.copyOf(result);
    }

    private static List<DefaultAccessor> createRecordAccessors(Class<?> type) {
        RecordComponent[] components = type.getRecordComponents();
        ArrayList<DefaultAccessor> result = new ArrayList<>(components.length);
        int ordinal = 0;
        for (RecordComponent descriptor : components) {
            String name = descriptor.getName();
            DefaultAccessor attribute = new DefaultAccessor(
                    descriptor.getType(),
                    name,
                    descriptor.getAccessor(),
                    null,
                    null,
                    ordinal++
            );
            result.add(attribute);
        }
        return List.copyOf(result);
    }

    private static boolean isSimpleType(Class<?> type) {
        return type.isPrimitive()
               || type.isEnum()
               || type.isArray()
               || type == String.class
               || Number.class.isAssignableFrom(type)
               || Boolean.class.isAssignableFrom(type)
               || Character.class.isAssignableFrom(type)
               || CharSequence.class.isAssignableFrom(type)
               || Collection.class.isAssignableFrom(type)
               || Map.class.isAssignableFrom(type)
               || Temporal.class.isAssignableFrom(type)
               || Date.class.isAssignableFrom(type);
    }

    /// 获取类型的所有声明字段（包括父类）
    private static Map<String, Field> getDeclaredFields(Class<?> clazz) {
        Map<String, Field> map = new LinkedHashMap<>();
        collectDeclaredFields(clazz, map);
        return map;
    }

    /// 递归收集声明字段
    private static void collectDeclaredFields(Class<?> clazz, Map<String, Field> map) {
        if (clazz == null || clazz == Object.class) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                map.putIfAbsent(field.getName(), field);
            }
        }
        collectDeclaredFields(clazz.getSuperclass(), map);
    }

}
