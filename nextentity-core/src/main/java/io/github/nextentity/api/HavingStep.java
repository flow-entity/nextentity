package io.github.nextentity.api;

/// 选择 Having 步骤接口，提供分组后的条件构建方法。
///
/// 继承 OrderByStep，用于在分组后添加过滤条件。
///
/// @param <T> 实体类型
/// @param <U> 结果类型
/// @author HuangChengwei
/// @see GroupByStep 分组示例包含 having 用法
/// @since 1.0.0
public interface HavingStep<T, U> extends OrderByStep<T, U> {

    /// 添加指定的分组条件断言。
    ///
    /// @param predicate 条件断言
    /// @return OrderByStep 实例
    OrderByStep<T, U> having(Expression<T, Boolean> predicate);

}