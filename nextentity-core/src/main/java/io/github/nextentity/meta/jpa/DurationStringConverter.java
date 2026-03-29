package io.github.nextentity.meta.jpa;

import io.github.nextentity.core.meta.ValueConverter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

///
/// Converter for {@link Duration} to/from {@link String}.
/// Duration values are stored as ISO-8601 formatted strings.
///
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
