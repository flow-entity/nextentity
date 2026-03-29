package io.github.nextentity.core.converter;

import io.github.nextentity.core.util.ImmutableList;

import java.util.List;

///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface TypeConverter {

    Object convert(Object value, Class<?> targetType);

    static TypeConverter ofDefault() {
        return of(NumberConverter.of(), EnumConverter.of(), LocalDateTimeConverter.of());
    }

    static TypeConverter of(TypeConverter... converters) {
        return new TypeConverters(ImmutableList.of(converters));
    }

    static TypeConverter of(List<TypeConverter> converters) {
        return new TypeConverters(converters);
    }

}
