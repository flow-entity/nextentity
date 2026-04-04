package io.github.nextentity.core.meta;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Attributes;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;

/// 实体模式接口，提供有关实体结构的元数据。
///
/// 该接口扩展 {@link Schema} 并提供实体特定的元数据，如
/// 表名、主键和乐观锁的版本属性。
///
/// 实体模式由查询构建器和持久化操作使用
/// 来理解实体结构和映射到数据库表。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface EntitySchema extends Schema {

    /// 获取此实体的主键（标识）属性。
    ///
    /// @return 标识属性
    EntityAttribute id();

    /// 获取此实体的数据库表名。
    ///
    /// @return 表名
    String tableName();

    /// 获取此实体的基本（非关联）属性。
    ///
    /// 此方法覆盖父模式方法，返回 {@link EntityAttribute} 实例
    /// 而不是通用属性。
    ///
    /// @return 基本实体属性的不可变数组
    @Override
    default ImmutableArray<? extends EntityAttribute> getPrimitives() {
        ImmutableArray<? extends Attribute> attributes = Schema.super.getPrimitives();
        return TypeCastUtil.cast(attributes);
    }

    /// 获取乐观锁的版本属性。
    ///
    /// 如果此实体不使用乐观锁，返回 null。
    ///
    /// @return 版本属性，如果不适用则返回 null
    EntityAttribute version();

    /// 获取此实体的所有属性，包括关联。
    ///
    /// @return 属性集合
    @Override
    Attributes attributes();
}
