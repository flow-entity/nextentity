package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.ValueConverter;

/// 身份值转换器（无转换）。
///
/// @param <T> 属性类型
/// @author HuangChengwei
/// @since 2.0.0
public class IdentityValueConverter<T> implements ValueConverter<T, T> {
    private static final IdentityValueConverter<?> INSTANCE = new IdentityValueConverter<>(Object.class);

    private final Class<T> type;

    public IdentityValueConverter(Class<T> type) {
        this.type = type;
    }

    public static <T> IdentityValueConverter<T> of(Class<T> type) {
        return new IdentityValueConverter<>(type);
    }

    public static ValueConverter<?, ?> of() {
        return INSTANCE;
    }

    @Override
    public T convertToDatabaseColumn(T attribute) {
        return attribute;
    }

    @Override
    public T convertToEntityAttribute(T dbData) {
        return dbData;
    }

    @Override
    public Class<? extends T> getDatabaseColumnType() {
        return type;
    }
}
