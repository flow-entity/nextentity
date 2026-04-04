package io.github.nextentity.core.converter;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/// LocalDateTime 类型转换器实现。
///
/// @author HuangChengwei
/// @since 1.0.0
public class LocalDateTimeConverter implements TypeConverter {

    private static final LocalDateTimeConverter INSTANCE = new LocalDateTimeConverter();

    protected LocalDateTimeConverter() {
    }

    public static LocalDateTimeConverter of() {
        return INSTANCE;
    }

    @Override
    public Object convert(Object value, Class<?> targetType) {
        if (value == null || targetType.isInstance(value)) {
            return value;
        }
        switch (value) {
            case java.sql.Date date when targetType == LocalDate.class -> {
                return date.toLocalDate();
            }
            case Timestamp timestamp when targetType == LocalDateTime.class -> {
                return timestamp.toLocalDateTime();
            }
            case Time time1 when targetType == LocalTime.class -> {
                return time1.toLocalTime();
            }
            case java.util.Date date -> {
                long time = date.getTime();
                if (targetType == LocalDate.class) {
                    return new java.sql.Date(time).toLocalDate();
                }
                if (targetType == LocalDateTime.class) {
                    return (new Timestamp(time)).toLocalDateTime();
                }
                if (targetType == LocalTime.class) {
                    return (new Time(time)).toLocalTime();
                }
            }
            default -> {
            }
        }
        return value;
    }
}
