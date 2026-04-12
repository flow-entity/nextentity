package io.github.nextentity.core.expression;

/// 表示 SQL 删除语句完整结构的不可变记录。
///
/// 包含 DELETE 查询的所有组件：目标实体、WHERE 条件。
///
/// DeleteStructure 实例是不可变的；所有修改方法返回新实例。
///
/// @param target 目标实体类型
/// @param where  WHERE 子句表达式
/// @author HuangChengwei
/// @since 2.1.0
public record DeleteStructure(
        Class<?> target,
        ExpressionNode where
) {

    /// 使用指定的目标实体创建 DeleteStructure。
    ///
    /// @param target 目标实体类型
    /// @return 新的 DeleteStructure 实例
    public static DeleteStructure of(Class<?> target) {
        return new DeleteStructure(target, EmptyNode.INSTANCE);
    }

    /// 使用指定的目标实体和 WHERE 条件创建 DeleteStructure。
    ///
    /// @param target 目标实体类型
    /// @param where  WHERE 条件
    /// @return 新的 DeleteStructure 实例
    public static DeleteStructure of(Class<?> target, ExpressionNode where) {
        return new DeleteStructure(target, where);
    }

    /// 返回更新了 WHERE 子句的新 DeleteStructure。
    ///
    /// @param where 新的 WHERE 表达式
    /// @return 新的 DeleteStructure 实例
    public DeleteStructure where(ExpressionNode where) {
        return new DeleteStructure(target, where);
    }

    /// 返回添加了 AND 条件的新 DeleteStructure。
    ///
    /// @param condition 要添加的条件
    /// @return 新的 DeleteStructure 实例
    public DeleteStructure and(ExpressionNode condition) {
        if (condition instanceof EmptyNode) {
            return this;
        }
        ExpressionNode newWhere = where instanceof EmptyNode ? condition : where.operate(Operator.AND, condition);
        return new DeleteStructure(target, newWhere);
    }

    /// 返回添加了 OR 条件的新 DeleteStructure。
    ///
    /// @param condition 要添加的条件
    /// @return 新的 DeleteStructure 实例
    public DeleteStructure or(ExpressionNode condition) {
        if (condition instanceof EmptyNode) {
            return this;
        }
        ExpressionNode newWhere = where instanceof EmptyNode ? condition : where.operate(Operator.OR, condition);
        return new DeleteStructure(target, newWhere);
    }

    /// 清空 WHERE 条件。
    ///
    /// @return 没有 WHERE 条件的新 DeleteStructure 实例
    public DeleteStructure clearWhere() {
        return new DeleteStructure(target, EmptyNode.INSTANCE);
    }
}