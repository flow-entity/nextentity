package io.github.nextentity.core.converter;

import io.github.nextentity.core.reflect.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// 枚举类型转换器实现。
///
/// @author HuangChengwei
/// @since 1.0.0
public class EnumConverter implements TypeConverter {

    private static final Logger log = LoggerFactory.getLogger(EnumConverter.class);
    private static final EnumConverter INSTANCE = new EnumConverter();

    public static EnumConverter of() {
        return INSTANCE;
    }

    @Override
    public Object convert(Object input, Class<?> targetType) {
        if (!targetType.isEnum() || input == null || targetType.isInstance(input)) {
            return input;
        }
        if (input instanceof String) {
            try {
                return ReflectUtil.getEnum(targetType, (String) input);
            } catch (Exception e) {
                log.warn("Enum conversion failed: {} -> {}", input, targetType, e);
            }
        }
        Object num = NumberConverter.of().convert(input, Integer.class);
        if (num instanceof Integer) {
            try {
                return ReflectUtil.getEnum(targetType, (Integer) num);
            } catch (Exception e) {
                log.warn("Enum conversion failed: {} -> {}", num, targetType, e);
            }
        }
        return input;
    }

    protected EnumConverter() {
    }

}
