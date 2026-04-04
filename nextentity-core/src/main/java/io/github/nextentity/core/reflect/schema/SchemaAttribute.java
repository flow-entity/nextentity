package io.github.nextentity.core.reflect.schema;

/// 也是模式的属性接口（嵌套类型）。
///
/// 此接口结合了 {@link Schema} 和 {@link Attribute} 来表示
/// 具有自己嵌套属性的属性，例如引用具有自身模式的另一个实体的
/// 关联字段（JoinAttribute）。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface SchemaAttribute extends Schema, Attribute {

}
