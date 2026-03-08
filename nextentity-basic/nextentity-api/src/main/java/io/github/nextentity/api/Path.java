package io.github.nextentity.api;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 路径接口，表示实体属性的路径引用。
 * <p>
 * 用于在查询中引用实体的属性，是一个函数式接口。
 *
 * @param <T> 实体类型
 * @param <R> 属性类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
@FunctionalInterface
public interface Path<T, R> extends Serializable {

    /**
     * 应用路径到实体，获取属性值。
     *
     * @param t 实体对象
     * @return 属性值
     */
    R apply(T t);

    /**
     * 数字引用接口，表示实体的数字类型属性路径。
     *
     * @param <T> 实体类型
     * @param <R> 数字类型
     */
    interface NumberRef<T, R extends Number> extends Path<T, R> {
    }

    /**
     * 布尔引用接口，表示实体的布尔类型属性路径。
     *
     * @param <T> 实体类型
     */
    interface BooleanRef<T> extends Path<T, Boolean> {
    }

    /**
     * 字符串引用接口，表示实体的字符串类型属性路径。
     *
     * @param <T> 实体类型
     */
    interface StringRef<T> extends Path<T, String> {
    }

    /**
     * 长整型引用接口，表示实体的长整型属性路径。
     *
     * @param <T> 实体类型
     */
    interface LongRef<T> extends NumberRef<T, Long> {
    }

    /**
     * 整型引用接口，表示实体的整型属性路径。
     *
     * @param <T> 实体类型
     */
    interface IntegerRef<T> extends NumberRef<T, Integer> {
    }

    /**
     * 短整型引用接口，表示实体的短整型属性路径。
     *
     * @param <T> 实体类型
     */
    interface ShortRef<T> extends NumberRef<T, Short> {
    }

    /**
     * 字节引用接口，表示实体的字节类型属性路径。
     *
     * @param <T> 实体类型
     */
    interface ByteRef<T> extends NumberRef<T, Byte> {
    }

    /**
     * 双精度浮点引用接口，表示实体的双精度浮点类型属性路径。
     *
     * @param <T> 实体类型
     */
    interface DoubleRef<T> extends NumberRef<T, Double> {
    }

    /**
     * 单精度浮点引用接口，表示实体的单精度浮点类型属性路径。
     *
     * @param <T> 实体类型
     */
    interface FloatRef<T> extends NumberRef<T, Float> {
    }

    /**
     *  BigDecimal引用接口，表示实体的BigDecimal类型属性路径。
     *
     * @param <T> 实体类型
     */
    interface BigDecimalRef<T> extends NumberRef<T, BigDecimal> {
    }

}
