package io.github.nextentity.api;

/**
 * 实体路径接口，表示实体之间的关联路径。
 * <p>
 * 继承自PathExpression，用于处理实体间的关联属性访问。
 *
 * @param <T> 实体类型
 * @param <U> 属性类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface EntityPath<T, U> extends PathExpression<T, U> {
    /**
     * 获取指定路径的子路径。
     *
     * @param path 路径
     * @param <R> 结果类型
     * @return 子路径
     */
    <R> io.github.nextentity.api.EntityPath<T, R> get(Path<U, R> path);

    /**
     * 获取指定字符串引用的字符串路径。
     *
     * @param path 字符串引用
     * @return 字符串路径
     */
    StringPath<T> get(Path.StringRef<U> path);

    /**
     * 获取指定数字引用的数字路径。
     *
     * @param path 数字引用
     * @param <R> 数字类型
     * @return 数字路径
     */
    <R extends Number> NumberPath<T, R> get(Path.NumberRef<U, R> path);

    /**
     * 获取指定路径表达式的子路径表达式。
     *
     * @param path 路径表达式
     * @param <R> 结果类型
     * @return 子路径表达式
     */
    <R> PathExpression<T, R> get(PathExpression<U, R> path);

    /**
     * 获取指定字符串路径的字符串路径。
     *
     * @param path 字符串路径
     * @return 字符串路径
     */
    StringPath<T> get(StringPath<U> path);

    /**
     * 获取指定布尔引用的布尔路径。
     *
     * @param path 布尔引用
     * @return 布尔路径
     */
    BooleanPath<T> get(Path.BooleanRef<T> path);

    /**
     * 获取指定数字路径的数字路径。
     *
     * @param path 数字路径
     * @param <R> 数字类型
     * @return 数字路径
     */
    <R extends Number> NumberPath<T, R> get(NumberPath<U, R> path);

}
