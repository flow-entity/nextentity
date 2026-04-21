package io.github.nextentity.core.expression;

/// 表示SELECT子句规范的密封接口。
///
/// 定义查询中选择哪些列/表达式以及是否应用DISTINCT。
///
/// 允许的子类型：
/// - {@link SelectEntity} - 选择实体的所有列
/// - {@link SelectExpression} - 选择单个表达式
/// - {@link SelectExpressions} - 选择多个表达式
/// - {@link SelectProjection} - 选择到DTO/投影类
///
/// @author HuangChengwei
/// @since 1.0.0
public sealed interface Selected permits SelectEntity, SelectExpression, SelectExpressions, SelectProjection, SelectNested {

    /// 指示是否对选择应用DISTINCT。
    ///
    /// @return 如果应应用DISTINCT则返回true
    boolean distinct();
}
