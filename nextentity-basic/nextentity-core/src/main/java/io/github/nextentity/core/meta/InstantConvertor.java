package io.github.nextentity.core.meta;

import java.sql.Timestamp;
import java.time.Instant;

public class InstantConvertor implements ValueConvertor<Instant, Timestamp> {

    private static final InstantConvertor INSTANCE = new InstantConvertor();

    public static InstantConvertor of() {
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
