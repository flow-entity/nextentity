package io.github.nextentity.core.meta;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Converter for {@link Instant} to/from {@link Timestamp}.
 */
public class InstantConverter implements ValueConverter<Instant, Timestamp> {

    private static final InstantConverter INSTANCE = new InstantConverter();

    public static InstantConverter of() {
        return INSTANCE;
    }

    @Override
    public Timestamp convertToDatabaseColumn(Instant attributeValue) {
        return attributeValue == null ? null : Timestamp.from(attributeValue);
    }

    @Override
    public Instant convertToEntityAttribute(Timestamp databaseValue) {
        return databaseValue == null ? null : databaseValue.toInstant();
    }

    @Override
    public Class<? extends Timestamp> getDatabaseColumnType() {
        return Timestamp.class;
    }
}
