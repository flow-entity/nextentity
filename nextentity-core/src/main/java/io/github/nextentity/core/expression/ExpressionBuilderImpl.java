package io.github.nextentity.core.expression;

import io.github.nextentity.api.ExpressionBuilder;

import java.util.function.Function;

/// 表达式构建器的具体实现类。
///
/// 继承自 AbstractExpressionBuilder 并实现了 ExpressionBuilder 接口。
///
/// @param <T> 实体类型
/// @param <U> 值类型
/// @param <B> 构建器返回类型
public class ExpressionBuilderImpl<T, U, B> extends AbstractExpressionBuilder<T, U, B> implements ExpressionBuilder<T, U, B> {

    /// 操作回调函数，用于处理表达式节点操作后的结果。
    protected final Function<? super ExpressionNode, ? extends B> operatedCallback;


    /// 构造函数，使用指定的目标节点和操作回调函数创建表达式构建器。
    ///
    /// @param target           目标表达式节点
    /// @param operatedCallback 操作完成后的回调函数
    public ExpressionBuilderImpl(ExpressionNode target, Function<? super ExpressionNode, ? extends B> operatedCallback) {
        super(target);
        this.operatedCallback = operatedCallback;
    }

    /// 处理表达式节点操作并返回构建器实例。
    ///
    /// @param operate 操作后的表达式节点
    /// @return 构建器实例
    protected B next(ExpressionNode operate) {
        return operatedCallback.apply(operate);
    }

    /// 检查值是否等于指定值（如果值不为空）。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    @Override
    public B eqIfNotNull(U value) {
        return value == null ? operateNull() : eq(value);
    }

}
