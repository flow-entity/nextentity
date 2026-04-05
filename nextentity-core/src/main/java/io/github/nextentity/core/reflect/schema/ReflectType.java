package io.github.nextentity.core.reflect.schema;

/// 反射类型信息的基接口。
///
/// 该密封接口是模式和属性元数据的类型层次结构的根。
/// 它允许两个子类型：
/// - {@link Attribute} - 表示单个字段/属性
/// - {@link Schema} - 表示具有多个属性的结构化类型
///
/// @author HuangChengwei
/// @since 1.0.0
public sealed interface ReflectType permits Attribute, Schema {

    /// 获取此反射类型表示的Java类型。
    ///
    /// @return Java类
    Class<?> type();

    /// 指示此类型是否为对象类型（具有属性）。
    ///
    /// @return 如果这是对象类型则返回true，如果是基本类型则返回false
    default boolean isObject() {
        return false;
    }

    /// 指示此类型是否为基本类型（没有属性）。
    ///
    /// @return 如果这是基本类型则返回true，如果是对象则返回false
    default boolean isPrimitive() {
        return !isObject();
    }

}
