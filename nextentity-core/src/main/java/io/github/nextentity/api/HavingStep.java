package io.github.nextentity.api;

/// 选择 Having 步骤接口，提供分组后的条件构建方法。
///
/// 继承 OrderByStep，用于在分组后添加过滤条件。
///
/// ## 使用示例
///
/// ```java
/// // 查询用户数超过 5 的部门
/// List<Tuple2<String, Long>> results = repository.query()
///     .select(User::getDepartment, User::getId.count())
///     .groupBy(User::getDepartment)
///     .having(User::getId.count().gt(5))
///     .getList();
///
/// // 多条件 Having
/// List<Tuple2<String, Long>> results = repository.query()
///     .select(User::getDepartment, User::getId.count())
///     .groupBy(User::getDepartment)
///     .having(Path.of(User::getId.count()).gt(5).and(Path.of(User::getId.count()).lt(100)))
///     .getList();
/// ```
///
/// @param <T> 实体类型
/// @param <U> 结果类型
/// @author HuangChengwei
/// @since 1.0.0
public interface HavingStep<T, U> extends OrderByStep<T, U> {

    /// 添加指定的分组条件断言。
    ///
    /// @param predicate 条件断言
    /// @return OrderByStep 实例
    OrderByStep<T, U> having(Expression<T, Boolean> predicate);

}