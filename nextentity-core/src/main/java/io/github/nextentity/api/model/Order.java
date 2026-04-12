package io.github.nextentity.api.model;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.SortOrder;

import java.io.Serializable;

/// 排序接口，定义排序的表达式和排序方向。
///
/// ## 使用示例
///
/// ```java
/// // 通过路径创建排序
/// Order<User> order1 = Path.of(User::getName).asc();
/// Order<User> order2 = Path.of(User::getAge).desc();
///
/// // 在查询中使用
/// List<User> users = repository.query()
///     .orderBy(order1, order2)
///     .list();
///
/// // 获取排序信息
/// Expression<User, ?> expr = order1.expression();
/// SortOrder direction = order1.order();
/// ```
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 1.0.0
public interface Order<T> extends Serializable {

    /// 获取排序表达式。
    ///
    /// @return 排序表达式
    Expression<T, ?> expression();

    /// 获取排序方向。
    ///
    /// @return 排序方向
    SortOrder order();

}