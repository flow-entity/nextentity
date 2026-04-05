package io.github.nextentity.core;

import io.github.nextentity.api.PathRef;
import io.github.nextentity.core.exception.ReflectiveException;

import java.io.Serializable;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/// 路径引用解析工具类，用于从方法引用中提取属性信息。
///
/// 该类通过解析序列化的 lambda 表达式来获取方法引用对应的字段名和类型信息，
/// 支持缓存以提高性能。
///
/// @author HuangChengwei
/// @since 1.0.0
public class PathReference {

    private static final Map<PathRef<?, ?>, PathReference> PATH_CACHE = new ConcurrentHashMap<>();

    private final String fieldName;
    private final Class<?> returnType;
    private final Class<?> entityType;

    private PathReference(SerializedLambda serializedLambda) {
        this.fieldName = getFieldName(serializedLambda.getImplMethodName());
        MethodType methodType = MethodType.fromMethodDescriptorString(
                serializedLambda.getInstantiatedMethodType(),
                Thread.currentThread().getContextClassLoader());
        returnType = methodType.returnType();
        entityType = methodType.parameterType(0);
    }

    /// 获取或创建给定路径的 PathReference。
    ///
    /// @param path 要获取引用的路径
    /// @return PathReference 实例
    ///
    public static <T, R> PathReference of(PathRef<T, R> path) {
        Objects.requireNonNull(path, "path must not be null");
        PathReference existing = PATH_CACHE.get(path);
        if (existing != null) {
            return existing;
        }
        PathReference newValue = createPathReference(path);
        PATH_CACHE.put(path, newValue);
        return newValue;
    }

    private static PathReference createPathReference(PathRef<?, ?> path) {
        try {
            Class<? extends Serializable> clazz = path.getClass();
            Method method = clazz.getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(path);
            int implMethodKind = serializedLambda.getImplMethodKind();
            if (implMethodKind != MethodHandleInfo.REF_invokeVirtual
                && implMethodKind != MethodHandleInfo.REF_invokeInterface) {
                throw new IllegalStateException(
                        "implMethodKind error: required "
                        + MethodHandleInfo.referenceKindToString(MethodHandleInfo.REF_invokeVirtual)
                        + " or " + MethodHandleInfo.referenceKindToString(MethodHandleInfo.REF_invokeInterface)
                        + " but is " + MethodHandleInfo.referenceKindToString(implMethodKind));
            }
            return new PathReference(serializedLambda);
        } catch (ReflectiveOperationException e) {
            throw new ReflectiveException(e);
        }
    }

    public static String getFieldName(String methodName) {
        Objects.requireNonNull(methodName, "methodName");
        StringBuilder builder;
        if (methodName.length() > 3 && methodName.startsWith("get")) {
            builder = new StringBuilder(methodName.substring(3));
        } else if (methodName.length() > 2 && methodName.startsWith("is")) {
            builder = new StringBuilder(methodName.substring(2));
        } else {
            return methodName;
        }
        if (builder.length() == 1) {
            return builder.toString().toLowerCase();
        }
        if (Character.isUpperCase(builder.charAt(1))) {
            return builder.toString();
        }
        builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
        return builder.toString();
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public Class<?> getReturnType() {
        return this.returnType;
    }

    public Class<?> getEntityType() {
        return this.entityType;
    }

    /// 清除路径缓存。在实体元数据更改时应调用此方法。
    ///
    public static void clearCache() {
        PATH_CACHE.clear();
    }
}
