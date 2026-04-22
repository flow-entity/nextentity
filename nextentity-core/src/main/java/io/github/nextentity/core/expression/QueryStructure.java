package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.LockModeType;

/// 表示 SQL 查询完整结构的不可变记录。
///
/// 包含 SELECT 查询的所有组件，包括选择、FROM 子句、
/// WHERE 子句、GROUP BY、ORDER BY、HAVING、分页（offset/limit）和锁模式。
///
/// QueryStructure 实例是不可变的；所有修改方法返回新实例。
///
/// @param select   选择规范（实体或投影）
/// @param from     FROM 子句规范（实体或子查询）
/// @param where    WHERE 子句表达式
/// @param groupBy  GROUP BY 表达式
/// @param orderBy  ORDER BY 表达式
/// @param having   HAVING 子句表达式
/// @param offset   分页的结果偏移量
/// @param limit    最大结果数
/// @param lockType JPA 锁模式类型
/// @author HuangChengwei
/// @since 1.0.0
public record QueryStructure(

        Selected select,

        From from,

        ExpressionNode where,

        ImmutableList<ExpressionNode> groupBy,

        ImmutableList<SortExpression> orderBy,

        ExpressionNode having,

        Integer offset,

        Integer limit,

        LockModeType lockType

) implements ExpressionNode {

    /// 使用指定的 select 和 from 子句创建 QueryStructure。
    ///
    /// @param select 选择规范
    /// @param from   FROM 子句规范
    /// @return 新的 QueryStructure 实例
    public static QueryStructure of(Selected select, From from) {
        return new QueryStructure(
                select,
                from,
                EmptyNode.INSTANCE,
                ImmutableList.of(),
                ImmutableList.of(),
                EmptyNode.INSTANCE,
                null,
                null,
                LockModeType.NONE);
    }

    /// 创建从实体类型选择的 QueryStructure。
    ///
    /// @param type 实体类
    /// @return 新的 QueryStructure 实例
    public static QueryStructure of(Class<?> type) {
        return new QueryStructure(
                new SelectEntity(ImmutableList.empty(), false),
                new FromEntity(type),
                EmptyNode.INSTANCE,
                ImmutableList.of(),
                ImmutableList.of(),
                EmptyNode.INSTANCE,
                null,
                null,
                LockModeType.NONE);
    }

    /// 返回更新了 select 和 from 子句的新 QueryStructure。
    ///
    /// @param select 新的选择规范
    /// @param from   新的 FROM 子句规范
    /// @return 新的 QueryStructure 实例
    public QueryStructure selectFrom(Selected select, From from) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /// 返回更新了 select 子句的新 QueryStructure。
    ///
    /// @param select 新的选择规范
    /// @return 新的 QueryStructure 实例
    public QueryStructure select(Selected select) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /// 返回更新了 FROM 子句的新 QueryStructure。
    ///
    /// @param from 新的 FROM 子句规范
    /// @return 新的 QueryStructure 实例
    public QueryStructure from(From from) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /// 返回更新了 WHERE 子句的新 QueryStructure。
    ///
    /// @param where 新的 WHERE 表达式
    /// @return 新的 QueryStructure 实例
    public QueryStructure where(ExpressionNode where) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /// 返回更新了 GROUP BY 表达式的新 QueryStructure。
    ///
    /// @param groupBy 新的 GROUP BY 表达式
    /// @return 新的 QueryStructure 实例
    public QueryStructure groupBy(ImmutableList<ExpressionNode> groupBy) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /// 返回更新了 ORDER BY 表达式的新 QueryStructure。
    ///
    /// @param orderBy 新的 ORDER BY 表达式
    /// @return 新的 QueryStructure 实例
    public QueryStructure orderBy(ImmutableList<SortExpression> orderBy) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /// 返回更新了 HAVING 子句的新 QueryStructure。
    ///
    /// @param having 新的 HAVING 表达式
    /// @return 新的 QueryStructure 实例
    public QueryStructure having(ExpressionNode having) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /// 返回更新了偏移量的新 QueryStructure。
    ///
    /// @param offset 新的偏移量值
    /// @return 新的 QueryStructure 实例
    public QueryStructure offset(Integer offset) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /// 返回更新了限制的新 QueryStructure。
    ///
    /// @param limit 新的限制值
    /// @return 新的 QueryStructure 实例
    public QueryStructure limit(Integer limit) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /// 返回更新了锁模式的新 QueryStructure。
    ///
    /// @param lockType 新的锁模式
    /// @return 新的 QueryStructure 实例
    public QueryStructure lockType(LockModeType lockType) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /// 返回移除了偏移量和限制的新 QueryStructure。
    ///
    /// 用于不需要分页的计数查询。
    ///
    /// @return 没有 offset/limit 的新 QueryStructure 实例
    public QueryStructure removeOffsetLimit() {
        if (offset == null && limit == null) {
            return this;
        }
        return new QueryStructure(select, from, where, groupBy, orderBy, having, null, null, lockType);
    }
}