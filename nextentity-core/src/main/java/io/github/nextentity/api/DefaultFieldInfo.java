package io.github.nextentity.api;

import io.github.nextentity.core.reflect.schema.Attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/// FieldInfo 默认实现。
///
/// @author HuangChengwei
/// @since 2.2.0
class DefaultFieldInfo implements FieldInfo {

    private final Attribute attribute;
    private final Class<?> projectionType;

    DefaultFieldInfo(Attribute attribute, Class<?> projectionType) {
        this.attribute = attribute;
        this.projectionType = projectionType;
    }

    @Override
    public Class<?> type() {
        return attribute.type();
    }

    @Override
    public String name() {
        return attribute.name();
    }

    @Override
    public Class<?> projectionType() {
        return projectionType;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        // 先从 Field 获取
        Field field = attribute.field();
        if (field != null) {
            A annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        // 再从 getter 方法获取
        Method getter = attribute.getter();
        if (getter != null) {
            return getter.getAnnotation(annotationClass);
        }
        return null;
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return getAnnotation(annotationClass) != null;
    }

    @Override
    public AnnotatedElement annotatedElement() {
        Field field = attribute.field();
        if (field != null) {
            return field;
        }
        Method getter = attribute.getter();
        return getter;
    }

    @Override
    public Field field() {
        return attribute.field();
    }

    @Override
    public Method getter() {
        return attribute.getter();
    }

    @Override
    public Attribute attribute() {
        return attribute;
    }
}