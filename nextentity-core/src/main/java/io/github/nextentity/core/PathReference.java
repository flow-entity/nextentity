package io.github.nextentity.core;

import io.github.nextentity.api.Path;
import io.github.nextentity.core.exception.ReflectiveException;

import java.io.Serializable;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class PathReference {

    private static final Map<Path<?, ?>, PathReference> PATH_CACHE = new ConcurrentHashMap<>();

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

    /**
     * Get or create a PathReference for the given path.
     *
     * @param path the path to get reference for
     * @return PathReference instance
     */
    public static <T, R> PathReference of(Path<T, R> path) {
        Objects.requireNonNull(path, "path must not be null");
        PathReference existing = PATH_CACHE.get(path);
        if (existing != null) {
            return existing;
        }
        PathReference newValue = createPathReference(path);
        PATH_CACHE.put(path, newValue);
        return newValue;
    }

    private static PathReference createPathReference(Path<?, ?> path) {
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

    /**
     * Clear the path cache. Should be called when entity metadata changes.
     */
    public static void clearCache() {
        PATH_CACHE.clear();
    }
}
