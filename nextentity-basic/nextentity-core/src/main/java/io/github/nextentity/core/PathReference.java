package io.github.nextentity.core;

import io.github.nextentity.api.Path;
import io.github.nextentity.core.exception.BeanReflectiveException;
import lombok.Getter;

import java.io.Serializable;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Getter
public class PathReference {

    private static final Pattern METHOD_PATTERN = Pattern.compile("^\\(L(.*);\\)(L(.*);|.*)$");

    private static final Map<Path<?, ?>, PathReference> map = new ConcurrentHashMap<>();

    private final String fieldName;
    private final Class<?> returnType;
    private final Class<?> entityType;

    private PathReference(SerializedLambda serializedLambda) {
        this.fieldName = getFieldName(serializedLambda.getImplMethodName());
        MethodType methodType = MethodType.fromMethodDescriptorString(
                serializedLambda.getInstantiatedMethodType(),
                serializedLambda.getClass().getClassLoader());
        returnType = methodType.returnType();
        entityType = methodType.parameterType(0);
    }

    public static <T, R> PathReference of(Path<T, R> path) {
        Objects.requireNonNull(path);
        PathReference v = map.get(path);
        if (v == null) {
            PathReference newValue = createPathReference(path);
            map.put(path, newValue);
            return newValue;
        }
        return v;
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
            throw new BeanReflectiveException(e);
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

}
