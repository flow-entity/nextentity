package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.ComplexAttribute;
import io.github.nextentity.core.meta.MetamodelAttribute;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

import java.util.*;

public class AttributeSet<T extends MetamodelAttribute> {
    private final Map<String, T> index;
    private final ImmutableArray<T> attributes;
    private final ImmutableArray<T> primitives;

    public AttributeSet(Collection<T> attributes) {
        index = new HashMap<>();
        this.attributes = ImmutableList.ofCollection(attributes);
        List<T> primitives = new ArrayList<>();
        for (T attribute : attributes) {
            index.put(attribute.name(), attribute);
            collectPrimitives(attribute, primitives);
        }
        this.primitives = ImmutableList.ofCollection(primitives);
    }

    /// 递归收集属性中的基本（非关联）属性。
    /// 如果属性本身是基本属性，直接添加到结果列表；
    /// 如果属性是嵌入类型，则递归遍历其内部的所有子属性，
    /// 将其中基本属性展开后收集到结果列表中。
    ///
    /// @param attribute 当前待收集的属性
    /// @param out       收集结果的输出列表
    private void collectPrimitives(MetamodelAttribute attribute, List<T> out) {
        if (attribute.isPrimitive()) {
            //noinspection unchecked
            out.add((T) attribute);
        } else if (attribute instanceof ComplexAttribute complex && complex.isEmbeddable()) {
            for (MetamodelAttribute child : complex.schema().getAttributes()) {
                collectPrimitives(child, out);
            }
        }
    }

    public Map<String, T> index() {
        return index;
    }

    public ImmutableArray<T> attributes() {
        return attributes;
    }

    public ImmutableArray<T> primitives() {
        return primitives;
    }
}
