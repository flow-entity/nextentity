package io.github.nextentity.api;

import java.util.Collection;
import java.util.List;

/**
 * 查询分组构建步骤接口，提供添加分组条件的方法。
 *
 * @param <T> 实体类型
 * @param <U> 查询结果类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public
interface SelectGroupByStep<T, U> extends SelectOrderByStep<T, U> {

    /**
     * 添加单个表达式作为分组条件。
     *
     * @param expressions 表达式
     * @return 查询分组后构建步骤
     */
    SelectHavingStep<T, U> groupBy(TypedExpression<T, ?> expressions);

    /**
     * 添加多个表达式作为分组条件。
     *
     * @param expressions 表达式列表
     * @return 查询分组后构建步骤
     */
    SelectHavingStep<T, U> groupBy(List<? extends TypedExpression<T, ?>> expressions);

    /**
     * 添加单个路径作为分组条件。
     *
     * @param path 路径
     * @return 查询分组后构建步骤
     */
    SelectHavingStep<T, U> groupBy(Path<T, ?> path);

    /**
     * 添加多个路径作为分组条件。
     *
     * @param paths 路径集合
     * @return 查询分组后构建步骤
     */
    SelectHavingStep<T, U> groupBy(Collection<Path<T, ?>> paths);

    /**
     * 添加两个路径作为分组条件。
     *
     * @param p0 第一个路径
     * @param p1 第二个路径
     * @return 查询分组后构建步骤
     */
    default SelectHavingStep<T, U> groupBy(Path<T, ?> p0, Path<T, ?> p1) {
        return groupBy(List.of(p0, p1));
    }

    /**
     * 添加三个路径作为分组条件。
     *
     * @param p0 第一个路径
     * @param p1 第二个路径
     * @param p2 第三个路径
     * @return 查询分组后构建步骤
     */
    default SelectHavingStep<T, U> groupBy(Path<T, ?> p0, Path<T, ?> p1, Path<T, ?> p2) {
        return groupBy(List.of(p0, p1, p2));
    }

    /**
     * 添加四个路径作为分组条件。
     *
     * @param p0 第一个路径
     * @param p1 第二个路径
     * @param p2 第三个路径
     * @param p3 第四个路径
     * @return 查询分组后构建步骤
     */
    default SelectHavingStep<T, U> groupBy(Path<T, ?> p0, Path<T, ?> p1, Path<T, ?> p2, Path<T, ?> p3) {
        return groupBy(List.of(p0, p1, p2, p3));
    }

    /**
     * 添加五个路径作为分组条件。
     *
     * @param p0 第一个路径
     * @param p1 第二个路径
     * @param p2 第三个路径
     * @param p3 第四个路径
     * @param p4 第五个路径
     * @return 查询分组后构建步骤
     */
    default SelectHavingStep<T, U> groupBy(Path<T, ?> p0, Path<T, ?> p1, Path<T, ?> p2, Path<T, ?> p3, Path<T, ?> p4) {
        return groupBy(List.of(p0, p1, p2, p3, p4));
    }

    /**
     * 添加六个路径作为分组条件。
     *
     * @param p0 第一个路径
     * @param p1 第二个路径
     * @param p2 第三个路径
     * @param p3 第四个路径
     * @param p4 第五个路径
     * @param p5 第六个路径
     * @return 查询分组后构建步骤
     */
    default SelectHavingStep<T, U> groupBy(Path<T, ?> p0, Path<T, ?> p1, Path<T, ?> p2, Path<T, ?> p3, Path<T, ?> p4, Path<T, ?> p5) {
        return groupBy(List.of(p0, p1, p2, p3, p4, p5));
    }
}
