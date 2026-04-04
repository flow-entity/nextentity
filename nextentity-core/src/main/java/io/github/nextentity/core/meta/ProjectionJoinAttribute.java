package io.github.nextentity.core.meta;

/// 用于映射投影关联字段的投影连接属性接口。
///
/// 此接口扩展 {@link ProjectionType}，表示映射到实体关联（连接属性）的投影字段。
///
/// 允许为复杂查询结果结构构建嵌套投影。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface ProjectionJoinAttribute extends ProjectionType {

    /// 获取此投影字段映射的源连接属性。
    ///
    /// @return 源连接属性
    JoinAttribute source();
}
