package io.github.nextentity.core.meta;

/// 返回未更改值的恒等值转换器。
///
/// 用于不需要在实体和数据库表示之间转换的类型，
/// 如 String、Integer 和其他基本类型。
///
/// 此转换器是单例，通过 {@link #of()} 获取共享实例。
///
/// @author HuangChengwei
/// @since 1.0.0
public class IdentityValueConverter implements ValueConverter<Object, Object> {

    /// 通用 Object 类型的共享单例实例。
    public static final IdentityValueConverter INSTANCE = new IdentityValueConverter(Object.class);

    private final Class<?> type;

    /// 返回单例实例。
    ///
    /// @return 共享的恒等转换器实例
    public static IdentityValueConverter of() {
        return INSTANCE;
    }

    /// 为特定类型创建新的恒等转换器。
    ///
    /// @param type 此转换器处理的类型
    public IdentityValueConverter(Class<?> type) {
        this.type = type;
    }

    /// 返回未更改的值。
    ///
    /// @param attributeValue 实体属性值
    /// @return 相同的值
    @Override
    public Object convertToDatabaseColumn(Object attributeValue) {
        return attributeValue;
    }

    /// 返回未更改的值。
    ///
    /// @param databaseValue 数据库列值
    /// @return 相同的值
    @Override
    public Object convertToEntityAttribute(Object databaseValue) {
        return databaseValue;
    }

    /// 返回此转换器处理的类型。
    ///
    /// @return 类型
    @Override
    public Class<?> getDatabaseColumnType() {
        return type;
    }
}
