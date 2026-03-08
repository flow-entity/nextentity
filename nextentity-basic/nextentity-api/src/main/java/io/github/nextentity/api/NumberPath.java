package io.github.nextentity.api;

/**
 * 数字路径接口，表示实体的数字类型属性路径。
 * <p>
 * 继承自NumberExpression和PathExpression，提供数字表达式操作和路径表达式功能。
 *
 * @param <T> 实体类型
 * @param <U> 数字类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface NumberPath<T, U extends Number> extends NumberExpression<T, U>, PathExpression<T, U> {
}
