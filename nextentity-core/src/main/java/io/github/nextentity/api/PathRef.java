package io.github.nextentity.api;

import java.io.Serializable;
import java.math.BigDecimal;

/// 路径引用接口，表示实体属性的路径引用。
///
/// 用于在查询中引用实体属性，是一个函数式接口。
///
/// ## 使用说明
///
/// 此接口及其所有子接口（NumberRef、BooleanRef、StringRef、LongRef 等）
/// 不打算由用户类直接实现。它们设计用于作为方法引用，
/// 在查询表达式中实现类型安全的属性访问。
///
/// ## 使用示例
///
/// ```java
/// // 使用方法引用作为路径表达式
/// repository.select(User.class)
///     .where(User::getId).eq(1L)
///     .getList();
///
/// // User::getId 自动转换为 Path<User, Long>
/// // User::getName 变为 Path<User, String> (StringRef)
/// // User::isActive 变为 Path<User, Boolean> (BooleanRef)
/// ```
///
/// @param <T> 实体类型
/// @param <R> 属性类型
/// @author HuangChengwei
/// @since 1.0.0
@FunctionalInterface
public interface PathRef<T, R> extends Serializable, Expression<T, R> {

    /// 将路径应用到实体并获取属性值。
    ///
    /// @param t 实体对象
    /// @return 属性值
    R apply(T t);

    /// 数值引用接口，表示实体数值类型属性的路径。
    ///
    /// @param <T> 实体类型
    /// @param <R> 数值类型
    interface NumberRef<T, R extends Number> extends PathRef<T, R> {
    }

    /// 布尔引用接口，表示实体布尔类型属性的路径。
    ///
    /// @param <T> 实体类型
    interface BooleanRef<T> extends PathRef<T, Boolean> {
    }

    /// 字符串引用接口，表示实体字符串类型属性的路径。
    ///
    /// @param <T> 实体类型
    interface StringRef<T> extends PathRef<T, String> {
    }

    /// Long 引用接口，表示实体 Long 类型属性的路径。
    ///
    /// @param <T> 实体类型
    interface LongRef<T> extends NumberRef<T, Long> {
    }

    /// Integer 引用接口，表示实体 Integer 类型属性的路径。
    ///
    /// @param <T> 实体类型
    interface IntegerRef<T> extends NumberRef<T, Integer> {
    }

    /// Short 引用接口，表示实体 Short 类型属性的路径。
    ///
    /// @param <T> 实体类型
    interface ShortRef<T> extends NumberRef<T, Short> {
    }

    /// Byte 引用接口，表示实体 Byte 类型属性的路径。
    ///
    /// @param <T> 实体类型
    interface ByteRef<T> extends NumberRef<T, Byte> {
    }

    /// Double 引用接口，表示实体 Double 类型属性的路径。
    ///
    /// @param <T> 实体类型
    interface DoubleRef<T> extends NumberRef<T, Double> {
    }

    /// Float 引用接口，表示实体 Float 类型属性的路径。
    ///
    /// @param <T> 实体类型
    interface FloatRef<T> extends NumberRef<T, Float> {
    }

    /// BigDecimal 引用接口，表示实体 BigDecimal 类型属性的路径。
    ///
    /// @param <T> 实体类型
    interface BigDecimalRef<T> extends NumberRef<T, BigDecimal> {
    }

    /// 实体路径引用接口，表示实体类型属性的路径。
    ///
    /// 用于在查询中访问嵌套实体。
    ///
    /// @param <T> 实体类型
    /// @param <R> 属性类型（必须是 Entity 类型）
    interface EntityPathRef<T, R extends Entity> extends PathRef<T, R> {
    }

}