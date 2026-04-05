package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.Predicate;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.util.Paths;

/// 表达式工具类，提供创建各种表达式的静态方法。
///
/// 注意：此类已废弃，请使用其他方式创建表达式。
///
/// @deprecated 请使用其他方式创建表达式
@Deprecated
public class Expressions {

    /// 创建包含字面值的表达式。
    ///
    /// 注意：此方法已废弃。
    ///
    /// @param <T>   实体类型
    /// @param <U>   值类型
    /// @param value 要包装的值
    /// @return 表达式实例
    @Deprecated
    public static <T, U> Expression<T, U> of(U value) {
        return Paths.<T>root().literal(value);
    }

    /// 创建恒真谓词。
    ///
    /// 注意：此方法已废弃。
    ///
    /// @param <T> 实体类型
    /// @return 恒真谓词实例
    @Deprecated
    public static <T> Predicate<T> ofTrue() {
        return new PredicateImpl<>(LiteralNode.TRUE);
    }

    /// 创建恒假谓词。
    ///
    /// 注意：此方法已废弃。
    ///
    /// @param <T> 实体类型
    /// @return 恒假谓词实例
    @Deprecated
    public static <T> Predicate<T> ofFalse() {
        return new PredicateImpl<>(LiteralNode.FALSE);
    }

    /// 从表达式创建谓词。
    ///
    /// 注意：此方法已废弃。
    ///
    /// @param <T>        实体类型
    /// @param expression 表达式
    /// @return 谓词实例
    @Deprecated
    public static <T> Predicate<T> ofPredicate(Expression expression) {
        return toTypedExpression(expression);
    }

    /// 将表达式转换为类型化的表达式。
    ///
    /// 注意：此方法已废弃。
    ///
    /// @param <T>        表达式类型
    /// @param expression 要转换的表达式
    /// @return 类型化的表达式
    @Deprecated
    static <T extends Expression<?, ?>> T toTypedExpression(Expression expression) {
        return TypeCastUtil.unsafeCast(expression);
    }

}
