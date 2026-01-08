package io.github.nextentity.api;

/**
 * 布尔路径接口，表示实体的布尔类型属性路径。
 *
 * @param <T> 实体类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface BooleanPath<T> extends Predicate<T>, PathExpression<T, Boolean> {
}
