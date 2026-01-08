package io.github.nextentity.api;

/**
 * 字符串路径接口，表示实体的字符串类型属性路径。
 *
 * @param <T> 实体类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface StringPath<T> extends StringExpression<T>, PathExpression<T, String> {
}
