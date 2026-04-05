package io.github.nextentity.core.reflect.schema;


import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.util.ImmutableList;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/// 表示模式字段/属性的属性接口。
///
/// 该接口提供关于单个属性的元数据，包括：
/// - 名称和类型
/// - getter 和 setter 方法
/// - 字段引用
/// - 声明模式
/// - 嵌套属性的路径
///
/// 还提供获取和设置实体实例上属性值的方法。
///
/// @author HuangChengwei
/// @since 1.0.0
public non-sealed interface Attribute extends ReflectType {

    /// 获取属性名称。
    ///
    /// @return 名称
    String name();

    /// 获取此属性的 getter 方法。
    ///
    /// @return getter 方法，如果不可用则返回 null
    Method getter();

    /// 获取此属性的 setter 方法。
    ///
    /// @return setter 方法，如果不可用则返回 null
    Method setter();

    /// 获取此属性的字段。
    ///
    /// @return 字段，如果不可用则返回 null
    Field field();

    /// 获取声明此属性的模式。
    ///
    /// @return 声明模式
    Schema declareBy();

    /// 获取此属性的路径。
    ///
    /// 对于嵌套属性，路径包括所有父属性名称。
    ///
    /// @return 属性路径，作为名称的不可变列表
    ImmutableList<String> path();

    /// 获取此属性的序号位置。
    ///
    /// @return 序号
    int ordinal();

    /// 获取此属性在路径层次结构中的深度。
    ///
    /// @return 路径深度
    default int deep() {
        return path().size();
    }

    /// 从实体实例获取属性值。
    ///
    /// 如果 getter 方法可访问则使用它，否则直接访问字段。
    ///
    /// @param entity 实体实例
    /// @return 属性值
    /// @throws ReflectiveException 如果访问失败
    default Object get(Object entity) {
        try {
            Method getter = getter();
            if (getter != null && ReflectUtil.isAccessible(getter, entity)) {
                return getter.invoke(entity);
            } else {
                return ReflectUtil.getFieldValue(field(), entity);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectiveException(e);
        }
    }

    /// 在实体实例上设置属性值。
    ///
    /// 如果 setter 方法可访问则使用它，否则直接设置字段。
    ///
    /// @param entity 实体实例
    /// @param value 要设置的值
    /// @throws ReflectiveException 如果访问失败
    default void set(Object entity, Object value) {
        try {
            Method setter = setter();
            if (setter != null && ReflectUtil.isAccessible(setter, entity)) {
                ReflectUtil.typeCheck(value, setter.getParameterTypes()[0]);
                setter.invoke(entity, value);
            } else {
                ReflectUtil.typeCheck(value, field().getType());
                ReflectUtil.setFieldValue(field(), entity, value);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectiveException(e);
        }
    }

}
