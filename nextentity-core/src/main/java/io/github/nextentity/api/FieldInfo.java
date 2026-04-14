package io.github.nextentity.api;

import io.github.nextentity.core.reflect.schema.Attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/// 字段信息接口，提供投影字段的完整元数据。
///
/// 用于 ProjectionFieldHandler 判断是否支持处理某字段，
/// 以及获取字段的类型、注解、名称等信息。
///
/// ## 功能
/// - 字段类型和名称
/// - 字段上的注解获取
/// - 字段所属的投影类
/// - Attribute 映射
///
/// @author HuangChengwei
/// @since 2.2.0
public interface FieldInfo {

    /// 获取字段的 Java 类型。
    ///
    /// @return 字段类型
    Class<?> type();

    /// 获取字段名称。
    ///
    /// @return 字段名
    String name();

    /// 获取字段所属的投影类。
    ///
    /// @return 投影类类型
    Class<?> projectionType();

    /// 获取字段上的指定注解。
    ///
    /// @param annotationClass 注解类型
    /// @return 注解实例，如果不存在返回 null
    <A extends Annotation> A getAnnotation(Class<A> annotationClass);

    /// 检查字段是否有指定注解。
    ///
    /// @param annotationClass 注解类型
    /// @return 如果存在返回 true
    boolean hasAnnotation(Class<? extends Annotation> annotationClass);

    /// 获取字段的 AnnotatedElement（Field 或 Method）。
    ///
    /// @return AnnotatedElement 实例
    AnnotatedElement annotatedElement();

    /// 获取字段的反射 Field 对象。
    ///
    /// 如果是接口属性，可能返回 null。
    ///
    /// @return Field 对象或 null
    Field field();

    /// 获取字段的 getter 方法。
    ///
    /// @return getter 方法或 null
    Method getter();

    /// 获取对应的 Attribute 定义。
    ///
    /// @return Attribute 实例
    Attribute attribute();

    /// 创建 FieldInfo 实例。
    ///
    /// @param attribute 投影属性
    /// @param projectionType 投影类
    /// @return FieldInfo 实例
    static FieldInfo of(Attribute attribute, Class<?> projectionType) {
        return new DefaultFieldInfo(attribute, projectionType);
    }
}