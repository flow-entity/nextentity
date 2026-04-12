package io.github.nextentity.api;

import java.util.Collection;
import java.util.List;

/// 查询分组步骤接口，提供添加分组条件的方法。
///
/// ## 使用示例
///
/// ```java
/// // 按单个字段分组
/// List<Tuple2<String, Long>> results = repository.query()
///     .select(User::getDepartment, User::getId.count())
///     .groupBy(User::getDepartment)
///     .list();
///
/// // 按多个字段分组
/// List<Tuple3<String, String, Long>> results = repository.query()
///     .select(User::getDepartment, User::getStatus, User::getId.count())
///     .groupBy(User::getDepartment, User::getStatus)
///     .list();
///
/// // 使用 having 过滤分组
/// List<Tuple2<String, Long>> results = repository.query()
///     .select(User::getDepartment, User::getId.count())
///     .groupBy(User::getDepartment)
///     .having(User::getId.count().gt(5))
///     .list();
/// ```
///
/// @param <T> 实体类型
/// @param <U> 查询结果类型
/// @author HuangChengwei
/// @since 1.0.0
public interface GroupByStep<T, U> extends OrderByStep<T, U> {

    /// 添加单个表达式作为分组条件。
    ///
    /// @param expressions 表达式
    /// @return 分组后的查询 Having 步骤
    HavingStep<T, U> groupByExpr(Expression<T, ?> expressions);

    /// 添加多个表达式作为分组条件。
    ///
    /// @param expressions 表达式列表
    /// @return 分组后的查询 Having 步骤
    HavingStep<T, U> groupByExpr(List<? extends Expression<T, ?>> expressions);

    /// 添加单个路径作为分组条件。
    ///
    /// @param path 路径
    /// @return 分组后的查询 Having 步骤
    HavingStep<T, U> groupBy(PathRef<T, ?> path);

    /// 添加多个路径作为分组条件。
    ///
    /// @param paths 路径集合
    /// @return 分组后的查询 Having 步骤
    HavingStep<T, U> groupBy(Collection<PathRef<T, ?>> paths);

    /// 添加两个路径作为分组条件。
    ///
    /// @param p0 第一个路径
    /// @param p1 第二个路径
    /// @return 分组后的查询 Having 步骤
    default HavingStep<T, U> groupBy(PathRef<T, ?> p0, PathRef<T, ?> p1) {
        return groupBy(List.of(p0, p1));
    }

    /// 添加三个路径作为分组条件。
    ///
    /// @param p0 第一个路径
    /// @param p1 第二个路径
    /// @param p2 第三个路径
    /// @return 分组后的查询 Having 步骤
    default HavingStep<T, U> groupBy(PathRef<T, ?> p0, PathRef<T, ?> p1, PathRef<T, ?> p2) {
        return groupBy(List.of(p0, p1, p2));
    }

    /// 添加四个路径作为分组条件。
    ///
    /// @param p0 第一个路径
    /// @param p1 第二个路径
    /// @param p2 第三个路径
    /// @param p3 第四个路径
    /// @return 分组后的查询 Having 步骤
    default HavingStep<T, U> groupBy(PathRef<T, ?> p0, PathRef<T, ?> p1, PathRef<T, ?> p2, PathRef<T, ?> p3) {
        return groupBy(List.of(p0, p1, p2, p3));
    }

    /// 添加五个路径作为分组条件。
    ///
    /// @param p0 第一个路径
    /// @param p1 第二个路径
    /// @param p2 第三个路径
    /// @param p3 第四个路径
    /// @param p4 第五个路径
    /// @return 分组后的查询 Having 步骤
    default HavingStep<T, U> groupBy(PathRef<T, ?> p0, PathRef<T, ?> p1, PathRef<T, ?> p2, PathRef<T, ?> p3, PathRef<T, ?> p4) {
        return groupBy(List.of(p0, p1, p2, p3, p4));
    }

    /// 添加六个路径作为分组条件。
    ///
    /// @param p0 第一个路径
    /// @param p1 第二个路径
    /// @param p2 第三个路径
    /// @param p3 第四个路径
    /// @param p4 第五个路径
    /// @param p5 第六个路径
    /// @return 分组后的查询 Having 步骤
    default HavingStep<T, U> groupBy(PathRef<T, ?> p0, PathRef<T, ?> p1, PathRef<T, ?> p2, PathRef<T, ?> p3, PathRef<T, ?> p4, PathRef<T, ?> p5) {
        return groupBy(List.of(p0, p1, p2, p3, p4, p5));
    }
}