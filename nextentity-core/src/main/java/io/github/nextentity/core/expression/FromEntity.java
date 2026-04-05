package io.github.nextentity.core.expression;

/// 表示从实体表查询的 FROM 子句记录。
///
/// 这是最常见的 FROM 子句类型，指定要查询其表的实体类。
///
/// @param type 实体类
/// @author HuangChengwei
/// @since 1.0.0
public record FromEntity(Class<?> type) implements From {
}