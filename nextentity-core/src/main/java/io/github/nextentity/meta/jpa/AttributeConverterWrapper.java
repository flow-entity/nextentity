package io.github.nextentity.meta.jpa;

import io.github.nextentity.core.meta.ValueConverter;
import jakarta.persistence.AttributeConverter;

///
/// JPA AttributeConverter 的包装器，实现了 ValueConverter 接口。
/// 该类用于将 JPA 的 AttributeConverter 适配为框架内部的 ValueConverter。
///
/// @param <X> 实体属性类型
/// @param <Y> 数据库列类型
/// @author HuangChengwei
/// @since 2.0.0
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
