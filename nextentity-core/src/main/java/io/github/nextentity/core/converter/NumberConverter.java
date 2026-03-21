package io.github.nextentity.core.converter;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.util.Maps;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;

/**
 * @author HuangChengwei
 * @since 1.0.0
 */
public class NumberConverter implements TypeConverter {

    private static final Map<Class<? extends Number>, Function<Number, Number>> CONVERTERS =
            Maps.<Class<? extends Number>, Function<Number, Number>>hashmap()
                    .put(int.class, Number::intValue)
                    .put(Integer.class, Number::intValue)
                    .put(long.class, Number::longValue)
                    .put(Long.class, Number::longValue)
                    .put(float.class, Number::floatValue)
                    .put(Float.class, Number::floatValue)
                    .put(double.class, Number::doubleValue)
                    .put(Double.class, Number::doubleValue)
                    .put(short.class, Number::shortValue)
                    .put(Short.class, Number::shortValue)
                    .put(byte.class, Number::byteValue)
                    .put(Byte.class, Number::byteValue)
                    .put(BigInteger.class, n -> new BigDecimal(String.valueOf(n)).toBigInteger())
                    .put(BigDecimal.class, n -> new BigDecimal(String.valueOf(n)))
                    .build();

    private static final NumberConverter INSTANCE = new NumberConverter();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(NumberConverter.class);

    public static NumberConverter of() {
        return INSTANCE;
    }

    protected NumberConverter() {
    }

    @Override
    public Object convert(Object value, Class<?> targetType) {
        if (value == null || targetType.isInstance(value)) {
            return value;
        }
        try {
            return doConvert(value, targetType);
        } catch (Exception e) {
            log.warn("{}[{}] cast to {} failed", value.getClass(), value, targetType, e);
            return value;
        }
    }

    private static Object doConvert(Object value, Class<?> targetType) {
        Number number;
        if (value instanceof String string) {
            try {
                number = new BigDecimal(string);
            } catch (NumberFormatException e) {
                log.warn("{}[{}] cast to {} failed", value.getClass(), string, targetType, e);
                return value;
            }
        } else if (value instanceof Number n) {
            number = n;
        } else {
            return value;
        }
        Function<Number, Number> indexOfTargetType = CONVERTERS.get(targetType);
        return indexOfTargetType == null ? value : indexOfTargetType.apply(number);
    }

    private static boolean isBasic(Class<?> targetType) {
        return targetType != BigDecimal.class && targetType != BigInteger.class;
    }

    private static boolean equals(Object a, Number b) {
        return a.getClass() == b.getClass() &&
               (TypeCastUtil.<Comparable<Object>>unsafeCast(a)).compareTo(b) == 0;
    }


}
