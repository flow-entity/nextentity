package io.github.nextentity.core.expression;

/// 表示从子查询选择的FROM子句的记录。
///
/// 当数据源是嵌套的SELECT语句而不是
/// 直接的实体表时使用。
///
/// @param structure 嵌套查询结构
/// @author HuangChengwei
/// @since 1.0.0
public record FromSubQuery(QueryStructure structure) implements From {
}
