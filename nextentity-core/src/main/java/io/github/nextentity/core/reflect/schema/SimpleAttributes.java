package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

import java.util.*;

/// {@link Attributes} 的简单实现。
///
/// 此类为属性集合提供具体的实现
/// 具有基于名称的查找和基本属性的分离。
///
/// @author HuangChengwei
/// @since 1.0.0
public class SimpleAttributes extends ImmutableList<Attribute> implements Attributes {

    private final Map<String, Attribute> index = new HashMap<>();
    private final ImmutableArray<Attribute> primitives;

    /// 从集合创建一个新的SimpleAttributes实例。
    ///
    /// 构建基于名称查找的索引并分离基本属性。
    ///
    /// @param attributes 要包含的属性
    public SimpleAttributes(Collection<? extends Attribute> attributes) {
        super(attributes);
        List<Attribute> primitives = new ArrayList<>();
        for (Attribute attribute : attributes) {
            index.put(attribute.name(), attribute);
            if (attribute.isPrimitive()) {
                primitives.add(attribute);
            }
        }
        this.primitives = ImmutableList.ofCollection(primitives);
    }

    /// 按名称获取属性。
    ///
    /// @param name 属性名称
    /// @return 属性，如果未找到则返回null
    @Override
    public Attribute get(String name) {
        return index.get(name);
    }

    /// 获取基本（非关联）属性。
    ///
    /// @return 不可变的基本属性数组
    @Override
    public ImmutableArray<Attribute> getPrimitives() {
        return primitives;
    }
}
