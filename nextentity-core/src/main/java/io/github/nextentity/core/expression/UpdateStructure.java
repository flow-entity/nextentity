package io.github.nextentity.core.expression;

import java.util.LinkedHashMap;
import java.util.Map;

/// 表示 SQL 更新语句完整结构的不可变记录。
///
/// 包含 UPDATE 查询的所有组件：目标实体、SET 子句、WHERE 条件。
///
/// UpdateStructure 实例是不可变的；所有修改方法返回新实例。
///
/// @param setClauses SET 子句条目的不可变列表
/// @param where      WHERE 子句表达式
/// @author HuangChengwei
/// @since 2.1.0
public record UpdateStructure(
        Map<String, Object> setClauses,
        ExpressionNode where
) {

    public static final UpdateStructure EMPTY = new UpdateStructure(
            Map.of(),
            EmptyNode.INSTANCE
    );

    /// 使用指定的目标实体创建 UpdateStructure。
    ///
    /// @return 新的 UpdateStructure 实例
    public static UpdateStructure of() {
        return EMPTY;
    }

    /// 返回添加了一个 SET 子句的新 UpdateStructure。
    ///
    /// @param name  要更新的字段名
    /// @param value 要设置的新值
    /// @return 新的 UpdateStructure 实例
    public UpdateStructure addSetClause(String name, Object value) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>(setClauses);
        map.put(name, value);
        return new UpdateStructure(map, where);
    }


    /// 返回更新了 WHERE 子句的新 UpdateStructure。
    ///
    /// @param where 新的 WHERE 表达式
    /// @return 新的 UpdateStructure 实例
    public UpdateStructure where(ExpressionNode where) {
        return new UpdateStructure(setClauses, where);
    }

    /// 返回添加了 AND 条件的新 UpdateStructure。
    ///
    /// @param condition 要添加的条件
    /// @return 新的 UpdateStructure 实例
    public UpdateStructure and(ExpressionNode condition) {
        if (condition instanceof EmptyNode) {
            return this;
        }
        ExpressionNode newWhere = where instanceof EmptyNode ? condition : where.operate(Operator.AND, condition);
        return new UpdateStructure(setClauses, newWhere);
    }

    /// 返回添加了 OR 条件的新 UpdateStructure。
    ///
    /// @param condition 要添加的条件
    /// @return 新的 UpdateStructure 实例
    public UpdateStructure or(ExpressionNode condition) {
        if (condition instanceof EmptyNode) {
            return this;
        }
        ExpressionNode newWhere = where instanceof EmptyNode ? condition : where.operate(Operator.OR, condition);
        return new UpdateStructure(setClauses, newWhere);
    }
}