package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.AbstractSchemaAttribute;
import io.github.nextentity.core.reflect.schema.Attributes;

import java.util.function.Function;

/// {@link ProjectionJoinAttribute} 的简单实现。
///
/// 此类为投影连接属性提供了具体实现，
/// 表示实体关联的嵌套投影映射。
///
/// 属性通过提供的函数延迟构建，以支持递归投影结构。
///
/// @author HuangChengwei
/// @since 1.0.0
public class SimpleProjectionJoinAttribute extends AbstractSchemaAttribute implements ProjectionJoinAttribute {

    private final JoinAttribute source;
    private final Function<? super SimpleProjectionJoinAttribute, Attributes> attributeBuilder;

    /// 创建新的 SimpleProjectionJoinAttribute 实例。
    ///
    /// @param source 源连接属性
    /// @param attributeBuilder 构建嵌套投影属性的函数
    public SimpleProjectionJoinAttribute(JoinAttribute source, Function<? super SimpleProjectionJoinAttribute, Attributes> attributeBuilder) {
        this.source = source;
        this.attributeBuilder = attributeBuilder;
    }

    /// 获取源连接属性。
    ///
    /// @return 源连接属性
    @Override
    public JoinAttribute source() {
        return source;
    }

    /// 延迟构建嵌套投影属性。
    ///
    /// @return 嵌套投影属性
    @Override
    protected Attributes buildAttributes() {
        return attributeBuilder.apply(this);
    }


}
