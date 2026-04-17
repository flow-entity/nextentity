package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.ValueConverter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/// LocalDateTime 时间日期转换器，将 java.sql.Timestamp 转换为 java.time.LocalDateTime。
///
/// @author HuangChengwei
/// @since 2.0.0
public class LocalDateTimeValueConverter implements ValueConverter<LocalDateTime, Timestamp> {
    private static final LocalDateTimeValueConverter INSTANCE = new LocalDateTimeValueConverter();

    public static LocalDateTimeValueConverter of() {
        return INSTANCE;
    }

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {
        return attribute != null ? Timestamp.valueOf(attribute) : null;
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
        return dbData != null ? dbData.toLocalDateTime() : null;
    }

    @Override
    public Class<Timestamp> getDatabaseColumnType() {
        return Timestamp.class;
    }
}
