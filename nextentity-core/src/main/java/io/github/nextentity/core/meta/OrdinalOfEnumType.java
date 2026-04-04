package io.github.nextentity.core.meta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/// 使用序数值作为数据库类型的枚举类型转换器。
///
/// 此类将枚举值转换为其序号位置（0, 1, 2, ...）用于数据库存储，
/// 并将序号整数转换回枚举值。
///
/// 当枚举在数据库中存储为整数时使用此转换器。
///
/// @author HuangChengwei
/// @since 1.0.0
public class OrdinalOfEnumType implements DatabaseType {

    private final Class<?> databaseType;
    private final Object[] values;

    /// 为给定的枚举类型创建新的 OrdinalOfEnumType 实例。
    ///
    /// @param attributeType 枚举类
    /// @throws RuntimeException 如果无法获取枚举值
    public OrdinalOfEnumType(Class<?> attributeType) {
        this.databaseType = Integer.class;
        try {
            Method method = attributeType.getDeclaredMethod("values");
            this.values = (Object[]) method.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /// 返回 Integer 作为数据库类型。
    ///
    /// @return Integer.class
    @Override
    public Class<?> databaseType() {
        return databaseType;
    }

    /// 将枚举值转换为其序号位置。
    ///
    /// @param value 枚举值
    /// @return 序号位置，如果值为 null 则返回 null
    @Override
    public Object toDatabaseType(Object value) {
        return value == null ? null : ((Enum<?>) value).ordinal();
    }

    /// 将序号位置转换为对应的枚举值。
    ///
    /// @param value 序号位置
    /// @return 枚举值，如果不是 Integer 则返回原始值
    @Override
    public Object toAttributeType(Object value) {
        return value instanceof Integer index ? values[index] : value;
    }
}
