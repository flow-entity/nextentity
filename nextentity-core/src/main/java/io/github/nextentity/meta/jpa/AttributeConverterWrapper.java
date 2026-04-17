package io.github.nextentity.meta.jpa;

import io.github.nextentity.core.meta.ValueConverter;
import jakarta.persistence.AttributeConverter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/// JPA AttributeConverter 的包装器，实现了 ValueConverter 接口。
/// 该类用于将 JPA 的 AttributeConverter 适配为框架内部的 ValueConverter。
///
/// @param <X> 实体属性类型
/// @param <Y> 数据库列类型
/// @author HuangChengwei
/// @since 2.0.0
public class AttributeConverterWrapper<X, Y> implements ValueConverter<X, Y> {

    private final AttributeConverter<X, Y> converter;
    private final Class<? extends Y> databaseColumnType;

    protected AttributeConverterWrapper(AttributeConverter<X, Y> converter) {
        this.converter = converter;
        this.databaseColumnType = resolveDatabaseColumnType(converter);
    }

    public static ValueConverter<?, ?> of(AttributeConverter<?, ?> converter) {
        if (converter instanceof ValueConverter) {
            return (ValueConverter<?, ?>) converter;
        }
        return new AttributeConverterWrapper<>(converter);
    }

    @Override
    public Y convertToDatabaseColumn(X attribute) {
        return converter.convertToDatabaseColumn(attribute);
    }

    @Override
    public X convertToEntityAttribute(Y dbData) {
        return converter.convertToEntityAttribute(dbData);
    }

    @Override
    public Class<? extends Y> getDatabaseColumnType() {
        return databaseColumnType;
    }

    @SuppressWarnings("unchecked")
    private static <Y> Class<? extends Y> resolveDatabaseColumnType(AttributeConverter<?, Y> converter) {
        Type converterType = resolveConverterType(converter.getClass());
        if (converterType instanceof ParameterizedType parameterizedType) {
            Type databaseType = parameterizedType.getActualTypeArguments()[1];
            if (databaseType instanceof Class<?> clazz) {
                return (Class<? extends Y>) clazz;
            }
            if (databaseType instanceof ParameterizedType nestedType
                && nestedType.getRawType() instanceof Class<?> rawType) {
                return (Class<? extends Y>) rawType;
            }
        }
        return null;
    }

    private static Type resolveConverterType(Class<?> type) {
        for (Type interfaceType : type.getGenericInterfaces()) {
            if (interfaceType instanceof ParameterizedType parameterizedType
                && parameterizedType.getRawType() == AttributeConverter.class) {
                return parameterizedType;
            }
        }
        Class<?> superclass = type.getSuperclass();
        return superclass != null ? resolveConverterType(superclass) : null;
    }
}
