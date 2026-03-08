package io.github.nextentity.api.model;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.SortOrder;

import java.io.Serializable;

/**
 * 排序接口，定义了排序的表达式和排序方向。
 *
 * @param <T> 实体类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Order<T> extends Serializable {

    /**
     * 获取排序表达式。
     *
     * @return 排序表达式
     */
    Expression expression();

    /**
     * 获取排序方向。
     *
     * @return 排序方向
     */
    SortOrder order();

}
