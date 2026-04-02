package io.github.nextentity.api;

import io.github.nextentity.api.ExpressionBuilder.NumberOperator;
import io.github.nextentity.api.ExpressionBuilder.PathOperator;
import io.github.nextentity.api.ExpressionBuilder.StringOperator;
import io.github.nextentity.api.PathRef.EntityPathRef;
import io.github.nextentity.api.PathRef.NumberRef;
import io.github.nextentity.api.PathRef.StringRef;

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
public interface BaseWhereStep<T, U> extends OrderByStep<T, U> {

    /// 添加布尔表达式作为查询条件。
    ///
    /// @param predicate 布尔表达式
    /// @return 查询条件构建步骤
    BaseWhereStep<T, U> where(Expression<T, Boolean> predicate);

    /// 添加路径作为查询条件。
    ///
    /// @param path 路径
    /// @param <N> 路径类型
    /// @return 路径操作器
    <N> PathOperator<T, N, ? extends BaseWhereStep<T, U>> where(PathRef<T, N> path);

    /// 添加数值路径作为查询条件。
    ///
    /// @param path 数值路径
    /// @param <N> 数值类型
    /// @return 数值操作器
    <N extends Number> NumberOperator<T, N, ? extends BaseWhereStep<T, U>> where(NumberRef<T, N> path);

    /// 添加字符串路径作为查询条件。
    ///
    /// @param path 字符串路径
    /// @return 字符串操作器
    StringOperator<T, ? extends BaseWhereStep<T, U>> where(StringRef<T> path);

    /// 添加路径表达式作为查询条件。
    ///
    /// @param path 路径表达式
    /// @param <N> 路径类型
    /// @return 路径操作器
    <N> PathOperator<T, N, ? extends BaseWhereStep<T, U>> where(Path<T, N> path);

    /// 添加数值路径作为查询条件。
    ///
    /// @param path 数值路径
    /// @param <N> 数值类型
    /// @return 数值操作器
    <N extends Number> NumberOperator<T, N, ? extends BaseWhereStep<T, U>> where(NumberPath<T, N> path);

    /// 添加字符串路径作为查询条件。
    ///
    /// @param path 字符串路径
    /// @return 字符串操作器
    StringOperator<T, ? extends BaseWhereStep<T, U>> where(StringPath<T> path);

    /// 添加实体路径作为查询条件，用于访问嵌套实体属性。
    ///
    /// 示例：
    /// ```java
    /// // Department 实现了 Entity 接口
    /// .where(User::getDepartment).get(Department::getName).eq("技术部")
    /// ```
    ///
    /// @param path 实体路径
    /// @param <R> 实体类型（必须实现 Entity 接口）
    /// @return 路径操作器，可继续调用 get() 访问嵌套属性
    default <R extends Entity> PathOperator<T, R, ? extends BaseWhereStep<T, U>> where(EntityPathRef<T, R> path) {
        return where((PathRef<T, R>) path);
    }


}