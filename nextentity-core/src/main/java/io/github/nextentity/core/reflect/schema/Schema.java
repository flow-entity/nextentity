package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.reflect.schema.impl.DefaultSchema;
import io.github.nextentity.core.util.ImmutableArray;

/// 表示具有属性的结构化类型的模式接口。
///
/// 该接口扩展 {@link ReflectType} 以提供对
/// 类的属性（字段/属性）的访问，支持嵌套路径
/// 遍历和属性发现。
///
/// Schema 是实体类型和投影类型的基接口。
///
/// @author HuangChengwei
/// @since 1.0.0
public non-sealed interface Schema extends ReflectType {

    static Schema of(Class<?> type) {
        return DefaultSchema.of(type);
    }

    /// 获取此模式的所有属性。
    ///
    /// @return 属性集合
    ImmutableArray<? extends Attribute> getAttributes();

    /// 获取此模式的基本（非关联）属性。
    ///
    /// 基本属性是直接映射到数据库列的简单字段，
    /// 而不是到其他实体的关联。
    ///
    /// @return 基本属性的不可变数组
    ImmutableArray<? extends Attribute> getPrimitives();

    /// 按名称获取属性。
    ///
    /// @param name 属性名称
    /// @return 属性
    /// @throws IllegalArgumentException 如果不存在具有给定名称的属性
    Attribute getAttribute(String name);

    /// 按字段名称的嵌套路径获取属性。
    ///
    /// 遍历嵌套模式以找到最终属性。
    ///
    /// @param fieldNames 字段名称路径
    /// @return 路径末端的属性
    /// @throws IllegalArgumentException 如果路径无效
    Attribute getAttribute(Iterable<String> fieldNames);

    /// 指示这是对象类型（非基本类型）。
    ///
    /// @return 对于模式类型始终返回 true
    default boolean isObject() {
        return true;
    }

}
