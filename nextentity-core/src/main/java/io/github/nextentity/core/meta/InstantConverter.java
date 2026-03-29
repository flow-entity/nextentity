package io.github.nextentity.core.meta;

import java.sql.Timestamp;
import java.time.Instant;

///
/// Converter for {@link Instant} to/from {@link Timestamp}.
///
/// Handles conversion between Java 8+ {@link Instant} and JDBC {@link Timestamp}
/// for database persistence.
///
/// This converter is a singleton with a shared instance available via {@link #of()}.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class InstantConverter implements ValueConverter<Instant, Timestamp> {

    private static final InstantConverter INSTANCE = new InstantConverter();

    ///
    /// Returns the singleton instance.
    ///
    /// @return the shared instant converter instance
    ///
    public static InstantConverter of() {
        return INSTANCE;
    }

    ///
    /// Converts an Instant to a Timestamp.
    ///
    /// @param attributeValue the Instant value
    /// @return the Timestamp value, or null if input is null
    ///
    @Override
    public Timestamp convertToDatabaseColumn(Instant attributeValue) {
        return attributeValue == null ? null : Timestamp.from(attributeValue);
    }

    ///
    /// Converts a Timestamp to an Instant.
    ///
    /// @param databaseValue the Timestamp value
    /// @return the Instant value, or null if input is null
    ///
    @Override
    public Instant convertToEntityAttribute(Timestamp databaseValue) {
        return databaseValue == null ? null : databaseValue.toInstant();
    }

    ///
    /// Returns Timestamp as the database column type.
    ///
    /// @return Timestamp.class
    ///
    @Override
    public Class<? extends Timestamp> getDatabaseColumnType() {
        return Timestamp.class;
    }
}
