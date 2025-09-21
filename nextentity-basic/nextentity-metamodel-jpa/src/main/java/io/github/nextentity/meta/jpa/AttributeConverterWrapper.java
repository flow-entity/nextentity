package io.github.nextentity.meta.jpa;

import io.github.nextentity.core.meta.ValueConvertor;
import jakarta.persistence.AttributeConverter;

public class AttributeConverterWrapper<X, Y> implements ValueConvertor<X, Y> {

    private final AttributeConverter<X, Y> convertor;

    protected AttributeConverterWrapper(AttributeConverter<X, Y> convertor) {
        this.convertor = convertor;
    }

    public static ValueConvertor<?, ?> of(AttributeConverter<?, ?> convertor) {
        if (convertor instanceof ValueConvertor) {
            return (ValueConvertor<?, ?>) convertor;
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
