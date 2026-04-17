package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.ValueConverter;

import java.sql.Date;
import java.time.LocalDate;

/// LocalDate 日期转换器，将 java.sql.Date 转换为 java.time.LocalDate。
///
/// @author HuangChengwei
/// @since 2.0.0
public class LocalDateValueConverter implements ValueConverter<LocalDate, Date> {
    private static final LocalDateValueConverter INSTANCE = new LocalDateValueConverter();

    public static LocalDateValueConverter of() {
        return INSTANCE;
    }

    @Override
    public Date convertToDatabaseColumn(LocalDate attribute) {
        return attribute != null ? Date.valueOf(attribute) : null;
    }

    @Override
    public LocalDate convertToEntityAttribute(Date dbData) {
        return dbData != null ? dbData.toLocalDate() : null;
    }

    @Override
    public Class<Date> getDatabaseColumnType() {
        return Date.class;
    }
}
