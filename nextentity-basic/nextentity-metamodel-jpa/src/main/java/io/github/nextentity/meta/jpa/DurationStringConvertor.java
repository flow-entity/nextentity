package io.github.nextentity.meta.jpa;

import io.github.nextentity.core.meta.ValueConvertor;
import jakarta.persistence.AttributeConverter;

import java.time.Duration;

public class DurationStringConvertor implements AttributeConverter<Duration, String>, ValueConvertor<Duration, String> {

    @Override
    public String convertToDatabaseColumn(Duration attributeValue) {
        return attributeValue == null ? null : attributeValue.toString();
    }

    @Override
    public Duration convertToEntityAttribute(String databaseValue) {
        return databaseValue == null ? null : Duration.parse(databaseValue);
    }

}
