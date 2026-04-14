package io.github.nextentity.core.meta;

import io.github.nextentity.api.EntityReference;
import io.github.nextentity.api.FieldTypeDescriptor;
import io.github.nextentity.core.annotation.ReferenceId;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableList;
import org.jspecify.annotations.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/// EntityReference 类型的投影属性。
///
/// 用于表示投影类中的 EntityReference 字段：
/// - 存储 ID 来源路径信息
/// - 提供目标实体类型和 ID 类型
/// - 支持延迟加载场景
///
/// ## ID 来源
/// - 自动推断: 字段名 + "Id" (如 user -> userId)
/// - @ReferenceId.value: 显式指定 ID 字段名
/// - @ReferenceId.path: 嵌套路径 (如 user.department.id)
///
/// @author HuangChengwei
/// @since 2.2.0
public class ReferenceAttribute implements Attribute {

    private final Attribute delegate;
    private final String idSourcePath;
    private final Class<?> targetType;
    private final Class<?> idType;
    private Schema declareBy;
    private Attribute sourceAttribute;
    private volatile ImmutableList<String> path;

    /// 创建 ReferenceAttribute。
    ///
    /// @param delegate 原始投影属性
    /// @param idSourcePath ID 来源路径
    /// @param targetType 目标实体类型
    /// @param idType ID 类型
    public ReferenceAttribute(@NonNull Attribute delegate, String idSourcePath,
                              Class<?> targetType, Class<?> idType) {
        this.delegate = delegate;
        this.idSourcePath = idSourcePath;
        this.targetType = targetType;
        this.idType = idType;
    }

    /// 从投影属性和注解创建 ReferenceAttribute。
    ///
    /// 自动解析：
    /// - 从 @ReferenceId 注解获取 ID 来源路径
    /// - 从 EntityReference 泛型参数获取目标类型和 ID 类型
    ///
    /// @param attribute 投影属性
    /// @return ReferenceAttribute 实例
    public static ReferenceAttribute from(Attribute attribute) {
        String idSourcePath = resolveIdSourcePath(attribute);
        GenericTypeInfo typeInfo = resolveGenericTypes(attribute.type());
        return new ReferenceAttribute(attribute, idSourcePath, typeInfo.entityType, typeInfo.idType);
    }

    /// 解析 ID 来源路径。
    private static String resolveIdSourcePath(Attribute attribute) {
        ReferenceId refId = getAnnotation(attribute, ReferenceId.class);

        if (refId != null) {
            if (!refId.value().isEmpty()) {
                return refId.value();
            }
            if (!refId.path().isEmpty()) {
                return refId.path();
            }
        }

        // 默认: 字段名 + "Id"
        return attribute.name() + "Id";
    }

    /// 从 EntityReference 子类解析泛型类型信息。
    private static GenericTypeInfo resolveGenericTypes(Class<?> fieldType) {
        Class<?> entityType = Object.class;
        Class<?> idType = Object.class;

        Type genericSuper = fieldType.getGenericSuperclass();
        while (genericSuper != null) {
            if (genericSuper instanceof ParameterizedType pt) {
                Type rawType = pt.getRawType();
                if (rawType == EntityReference.class) {
                    Type[] args = pt.getActualTypeArguments();
                    if (args.length >= 1) {
                        entityType = resolveClass(args[0]);
                    }
                    if (args.length >= 2) {
                        idType = resolveClass(args[1]);
                    }
                    break;
                }
            }

            Class<?> rawClass = genericSuper instanceof Class<?> c ? c :
                    genericSuper instanceof ParameterizedType pt ?
                    (Class<?>) pt.getRawType() : null;
            if (rawClass != null && rawClass != Object.class) {
                genericSuper = rawClass.getGenericSuperclass();
            } else {
                break;
            }
        }

        return new GenericTypeInfo(entityType, idType);
    }

    private static Class<?> resolveClass(Type type) {
        if (type instanceof Class<?> c) {
            return c;
        } else if (type instanceof ParameterizedType pt) {
            return (Class<?>) pt.getRawType();
        }
        return Object.class;
    }

    /// 获取属性上的注解。
    private static <A extends Annotation> A getAnnotation(Attribute attribute, Class<A> annotationClass) {
        A annotation = null;
        if (attribute.field() != null) {
            annotation = attribute.field().getAnnotation(annotationClass);
        }
        if (annotation == null && attribute.getter() != null) {
            annotation = attribute.getter().getAnnotation(annotationClass);
        }
        return annotation;
    }

    /// 获取 ID 来源路径。
    public String idSourcePath() {
        return idSourcePath;
    }

    /// 获取目标实体类型。
    public Class<?> targetType() {
        return targetType;
    }

    /// 获取 ID 类型。
    public Class<?> idType() {
        return idType;
    }

    /// 获取 ID 来源属性（从源实体中获取）。
    public Attribute sourceAttribute() {
        return sourceAttribute;
    }

    /// 设置 ID 来源属性。
    public void setSourceAttribute(Attribute sourceAttribute) {
        this.sourceAttribute = sourceAttribute;
    }

    /// 设置声明模式。
    public ReferenceAttribute declareBy(Schema declareBy) {
        this.declareBy = declareBy;
        return this;
    }

    /// 创建字段类型描述符。
    public FieldTypeDescriptor createDescriptor() {
        return FieldTypeDescriptor.builder()
                .fieldType(delegate.type())
                .targetType(targetType)
                .idType(idType)
                .idSourcePath(idSourcePath)
                .isNested(false)
                .requiresJoin(false)
                .build();
    }

    // Implement Attribute interface methods
    @Override
    public Class<?> type() {
        return delegate.type();
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public Method getter() {
        return delegate.getter();
    }

    @Override
    public Method setter() {
        return delegate.setter();
    }

    @Override
    public Field field() {
        return delegate.field();
    }

    @Override
    public Schema declareBy() {
        return declareBy;
    }

    @Override
    public ImmutableList<String> path() {
        if (path == null) {
            synchronized (this) {
                if (path == null) {
                    if (declareBy instanceof Attribute p) {
                        ImmutableList<String> pp = p.path();
                        String[] strings = new String[pp.size() + 1];
                        for (int i = 0; i < pp.size(); i++) {
                            strings[i] = pp.get(i);
                        }
                        strings[pp.size()] = name();
                        path = ImmutableList.of(strings);
                    } else {
                        path = ImmutableList.of(name());
                    }
                }
            }
        }
        return path;
    }

    @Override
    public int ordinal() {
        return delegate.ordinal();
    }

    /// 泛型类型信息。
    private static class GenericTypeInfo {
        final Class<?> entityType;
        final Class<?> idType;

        GenericTypeInfo(Class<?> entityType, Class<?> idType) {
            this.entityType = entityType;
            this.idType = idType;
        }
    }
}