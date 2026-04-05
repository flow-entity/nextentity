package io.github.nextentity.core.expression;

/// 提供访问表达式树根节点的接口。
///
/// 由表达式构建器和类型化表达式实现，暴露底层的表达式树结构用于查询构建。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface ExpressionTree {

    ///
    /// 获取此表达式树的根节点。
    ///
    /// @return 根表达式节点
    ///
    ExpressionNode getRoot();
}