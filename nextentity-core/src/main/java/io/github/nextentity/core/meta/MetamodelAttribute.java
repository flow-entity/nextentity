package io.github.nextentity.core.meta;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.reflect.schema.Accessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface MetamodelAttribute {

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

    Accessor accessor();

    /// 获取属性名称。
    ///
    /// @return 名称
    default String name() {
        return accessor().name();
    }

    /// 获取此属性的 getter 方法。
    ///
    /// @return getter 方法，如果不可用则返回 null
    default Method getter() {
        return accessor().getter();
    }

    /// 获取此属性的 setter 方法。
    ///
    /// @return setter 方法，如果不可用则返回 null
    default Method setter() {
        return accessor().setter();
    }

    /// 获取此属性的字段。
    ///
    /// @return 字段，如果不可用则返回 null
    default Field field() {
        return accessor().field();
    }

    /// 获取声明此属性的模式。
    ///
    /// @return 声明模式
    MetamodelSchema<?> declareBy();

    /// 获取此属性的路径。
    ///
    /// 对于嵌套属性，路径包括所有父属性名称。
    ///
    /// @return 属性路径，作为名称的不可变列表
    PathNode path();

    /// 获取此属性在路径层次结构中的深度。
    ///
    /// @return 路径深度
    default int deep() {
        return path().size();
    }

    /// 从指定对象实例中获取此属性的值。
    ///
    /// 对象实例可以是根实体、嵌入对象或投影实例，只要它包含此属性的声明。
    /// 如果 getter 方法可访问则使用它，否则直接访问字段。
    ///
    /// @param instance 包含此属性的对象实例
    /// @return 属性值
    /// @throws ReflectiveException 如果访问失败
    default Object get(Object instance) {
        return accessor().get(instance);
    }

    /// 从根实体开始沿声明链向下导航，获取此属性的值。
    ///
    /// 与 {@link #get(Object)} 要求传入声明类型的实例不同，此方法
    /// 接受根实体实例，自动遍历 {@code @Embedded} 属性链直到当前层级。
    /// 例如对于 {@code address.zipCode} 属性，传入 {@code Person} 实例即可，
    /// 方法会先获取 {@code address} 再获取 {@code zipCode}。
    ///
    /// @param root 根实体实例
    /// @return 属性值
    /// @throws ReflectiveException 如果访问失败
    default Object getFromRoot(Object root) {
        MetamodelSchema<?> schema = declareBy();
        Object declared = root;
        if (schema instanceof MetamodelAttribute parent) {
            declared = parent.getFromRoot(declared);
            if (declared == null) {
                return null;
            }
        }
        return accessor().get(declared);
    }

    /// 在指定对象实例上设置属性值。
    ///
    /// 对象实例可以是根实体、嵌入对象或投影实例，只要它包含此属性的声明。
    /// 如果 setter 方法可访问则使用它，否则直接设置字段。
    ///
    /// @param instance 包含此属性的对象实例
    /// @param value    要设置的值
    /// @throws ReflectiveException 如果访问失败
    default void set(Object instance, Object value) {
        accessor().set(instance, value);
    }

}
