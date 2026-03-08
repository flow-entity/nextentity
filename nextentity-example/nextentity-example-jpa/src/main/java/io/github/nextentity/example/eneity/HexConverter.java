package io.github.nextentity.example.eneity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class HexConverter implements AttributeConverter<Hex, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(Hex attribute) {
        return attribute == null ? null : attribute.id;
    }

    @Override
    public Hex convertToEntityAttribute(byte[] dbData) {
        return dbData == null ? null : new Hex(dbData);
    }
}
