package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import jakarta.persistence.FetchType;

/// 元模型 Schema 基础接口，定义类型安全的属性集合访问。
///
/// 是 {@link EntitySchema} 和 {@link ProjectionSchema} 的公共父接口，
/// 提供属性的集合访问、按名称查找、嵌套路径查找和懒加载检测等通用能力。
///
/// @param <T> 属性类型，必须是 {@link Attribute} 的子类型
/// @see EntitySchema
/// @see ProjectionSchema
public interface MetamodelSchema<T extends MetamodelAttribute> extends Schema {
    /// 获取此模式的所有属性。
    ///
    /// @return 属性集合
    ImmutableArray<? extends T> getAttributes();

    /// 获取此模式的基本（非关联）属性。
    ///
    /// 基本属性是直接映射到数据库列的简单字段，
    /// 而不是到其他实体的关联。
    ///
    /// @return 基本属性的不可变数组
    ImmutableArray<? extends T> getPrimitives();

    /// 按名称获取属性。
    ///
    /// @param name 属性名称
    /// @return 属性
    /// @throws IllegalArgumentException 如果不存在具有给定名称的属性
    T getAttribute(String name);

    /// 按字段名称的嵌套路径获取属性。
    ///
    /// 遍历嵌套模式以找到最终属性。
    ///
    /// @param fieldNames 字段名称路径
    /// @return 路径末端的属性
    /// @throws IllegalArgumentException 如果路径无效
    T getAttribute(Iterable<String> fieldNames);

    /// 检查投影是否包含懒加载属性
    ///
    /// 遍历投影的所有属性，检查是否有 FetchType.LAZY 的嵌套属性。
    /// 如果存在懒加载属性，需要创建代理对象来支持延迟加载。
    ///
    /// @return true 表示存在懒加载属性，false 表示全部为立即加载
    default boolean hasLazyAttribute() {
        for (Attribute attr : getAttributes()) {
            if (attr instanceof JoinAttribute joinAttribute) {
                if (joinAttribute.getFetchType() == FetchType.LAZY) {
                    return true;
                }
            }
        }
        return false;
    }
}
