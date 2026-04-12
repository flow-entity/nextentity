package io.github.nextentity.api;

import java.io.Serializable;
import java.math.BigDecimal;

/// 路径引用接口，用于方法引用的类型匹配。
///
/// 此接口及其子接口设计用于作为方法引用，实现类型安全的属性访问。
/// 不打算由用户类直接实现。
///
/// @param <T> 实体类型
/// @param <R> 属性类型
/// @author HuangChengwei
/// @see Path 路径表达式创建和使用示例
/// @since 1.0.0
@FunctionalInterface
public interface PathRef<T, R> extends Serializable, Expression<T, R> {

    /// 将路径应用到实体并获取属性值。
    ///
    /// @param t 实体对象
    /// @return 属性值
    R apply(T t);

    /// 数值引用类型标记接口。
    interface NumberRef<T, R extends Number> extends PathRef<T, R> {
    }

    /// 布尔引用类型标记接口。
    interface BooleanRef<T> extends PathRef<T, Boolean> {
    }

    /// 字符串引用类型标记接口。
    interface StringRef<T> extends PathRef<T, String> {
    }

    /// Long 引用类型标记接口。
    interface LongRef<T> extends NumberRef<T, Long> {
    }

    /// Integer 引用类型标记接口。
    interface IntegerRef<T> extends NumberRef<T, Integer> {
    }

    /// Short 引用类型标记接口。
    interface ShortRef<T> extends NumberRef<T, Short> {
    }

    /// Byte 引用类型标记接口。
    interface ByteRef<T> extends NumberRef<T, Byte> {
    }

    /// Double 引用类型标记接口。
    interface DoubleRef<T> extends NumberRef<T, Double> {
    }

    /// Float 引用类型标记接口。
    interface FloatRef<T> extends NumberRef<T, Float> {
    }

    /// BigDecimal 引用类型标记接口。
    interface BigDecimalRef<T> extends NumberRef<T, BigDecimal> {
    }

    /// 实体路径引用，用于访问嵌套实体。
    interface EntityPathRef<T, R extends Entity> extends PathRef<T, R> {
    }

}