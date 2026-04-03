package io.github.nextentity.meta.jpa;

import io.github.nextentity.core.meta.ValueConverter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

///
/// Duration 与 String 之间的转换器。
/// Duration 值以 ISO-8601 格式字符串存储。
///
/// @author HuangChengwei
/// @since 2.1
@Converter
public class DurationStringConverter implements AttributeConverter<Duration, String>, ValueConverter<Duration, String> {

    @Override
    public String convertToDatabaseColumn(Duration attributeValue) {
        return attributeValue == null ? null : attributeValue.toString();
    }

    @Override
    public Duration convertToEntityAttribute(String databaseValue) {
        return databaseValue == null ? null : Duration.parse(databaseValue);
    }

}
