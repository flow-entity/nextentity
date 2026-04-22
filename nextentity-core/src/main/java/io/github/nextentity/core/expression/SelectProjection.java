package io.github.nextentity.core.expression;

/// 表示将结果映射到投影/DTO类的SELECT子句的记录。
///
/// 当查询结果应映射到非实体类（如
/// DTO、记录或接口投影）时使用。
///
/// @param type     投影类
/// @param distinct 是否应用DISTINCT
/// @author HuangChengwei
/// @since 1.0.0
public record SelectProjection(Class<?> type, boolean distinct) implements Selected {
}
