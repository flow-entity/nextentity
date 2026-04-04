package io.github.nextentity.api;

/// 查询条件构建步骤接口，提供添加查询条件的方法。
///
/// ## 使用示例
///
/// ```java
/// // 简单条件
/// List<User> users = repository.query()
///     .where(User::getAge).gt(18)
///     .getList();
///
/// // 多条件组合
/// List<User> users = repository.query()
///     .where(User::getAge).gt(18)
///     .where(User::getStatus).eq("ACTIVE")
///     .getList();
///
/// // 使用断言
/// Predicate<User> predicate = Path.of(User::getAge).gt(18);
/// List<User> users = repository.query()
///     .where(predicate)
///     .getList();
/// ```
///
/// @param <T> 实体类型
/// @param <U> 查询结果类型
/// @author HuangChengwei
/// @since 1.0.0
public interface BaseWhereStep<T, U> extends Whereable<T, BaseWhereStep<T, U>>, OrderByStep<T, U> {

}