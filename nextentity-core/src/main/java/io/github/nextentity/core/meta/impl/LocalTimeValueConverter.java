package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.ValueConverter;

import java.sql.Time;
import java.time.LocalTime;

/// LocalTime 时间转换器，将 java.sql.Time 转换为 java.time.LocalTime。
///
/// @author HuangChengwei
/// @since 2.0.0
public class LocalTimeValueConverter implements ValueConverter<LocalTime, Time> {
    private static final LocalTimeValueConverter INSTANCE = new LocalTimeValueConverter();

    public static LocalTimeValueConverter of() {
        return INSTANCE;
    }

    @Override
    public Time convertToDatabaseColumn(LocalTime attribute) {
        return attribute != null ? Time.valueOf(attribute) : null;
    }

    @Override
    public LocalTime convertToEntityAttribute(Time dbData) {
        return dbData != null ? dbData.toLocalTime() : null;
    }

    @Override
    public Class<Time> getDatabaseColumnType() {
        return Time.class;
    }
}
