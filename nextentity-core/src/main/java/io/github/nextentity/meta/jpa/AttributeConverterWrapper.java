package io.github.nextentity.meta.jpa;

import io.github.nextentity.core.meta.ValueConverter;
import jakarta.persistence.AttributeConverter;

public class AttributeConverterWrapper<X, Y> implements ValueConverter<X, Y> {

    private final AttributeConverter<X, Y> convertor;

    protected AttributeConverterWrapper(AttributeConverter<X, Y> convertor) {
        this.convertor = convertor;
    }

    public static ValueConverter<?, ?> of(AttributeConverter<?, ?> convertor) {
        if (convertor instanceof ValueConverter) {
            return (ValueConverter<?, ?>) convertor;
        } else {
            return new AttributeConverterWrapper<>(convertor);
        }
    }

    @Override
    public Y convertToDatabaseColumn(X attribute) {
        return convertor.convertToDatabaseColumn(attribute);
    }

    @Override
    public X convertToEntityAttribute(Y dbData) {
        return convertor.convertToEntityAttribute(dbData);
    }

}
