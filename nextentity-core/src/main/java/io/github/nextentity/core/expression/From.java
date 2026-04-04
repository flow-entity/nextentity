package io.github.nextentity.core.expression;

/// 表示查询 FROM 子句的密封接口。
///
/// 允许的子类型：
/// - {@link FromEntity}：从实体表查询
/// - {@link FromSubQuery}：从子查询查询
///
/// @author HuangChengwei
/// @since 1.0.0
public sealed interface From permits FromEntity, FromSubQuery {
}