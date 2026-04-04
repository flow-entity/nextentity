package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableArray;

/// 模式属性的集合接口。
///
/// 此接口扩展了 {@link ImmutableArray} 以提供对属性的命名访问
/// 以及分离基本属性和关联属性。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface Attributes extends ImmutableArray<Attribute> {

    /// 返回指定名称的属性。
    ///
    /// @param name 属性的名称
    /// @return 指定名称的属性，如果未找到则返回null
    Attribute get(String name);

    /// 获取基本（非关联）属性。
    ///
    /// 基本属性是直接映射到数据库列的简单字段，
    /// 而不是与其他实体的关联。
    ///
    /// @return 不可变的基本属性数组
    ImmutableArray<Attribute> getPrimitives();

}
