package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.Path;
import io.github.nextentity.api.PathRef;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/// 表达式节点工具类，提供处理表达式节点的各种静态方法。
public class ExpressionNodes {

    /// 检查表达式是否为空或者为真值。
    ///
    /// @param expression 要检查的表达式
    /// @return 如果表达式为空或为真值则返回true，否则返回false
    public static boolean isNullOrTrue(Expression<?, ?> expression) {
        if (expression == null) {
            return true;
        }
        ExpressionNode node = getNode(expression);
        return isNullOrTrue(node);
    }

    /// 检查表达式节点是否为空或者为真值。
    ///
    /// @param node 要检查的表达式节点
    /// @return 如果节点为空或为真值则返回true，否则返回false
    public static boolean isNullOrTrue(ExpressionNode node) {
        return node == null || node == EmptyNode.INSTANCE
               || (node instanceof LiteralNode(Object value)) && Boolean.TRUE.equals(value);
    }

    /// 获取表达式的根节点。
    ///
    /// @param expression 表达式
    /// @return 表达式根节点
    /// @throws IllegalArgumentException 当表达式类型不支持时抛出
    public static ExpressionNode getNode(Expression<?, ?> expression) {
        ExpressionTree tree;
        if (expression instanceof ExpressionTree expressionTree) {
            tree = expressionTree;
        } else if (expression instanceof PathRef<?, ?> path) {
            tree = (ExpressionTree) Path.of(path);
        } else {
            throw new IllegalArgumentException(expression.getClass().getName());
        }
        return tree.getRoot();
    }

    /// 获取表达式列表的节点列表。
    ///
    /// @param expressions 表达式列表
    /// @return 表达式节点的不可变列表
    public static ImmutableList<ExpressionNode> getNode(List<? extends Expression<?, ?>> expressions) {
        return expressions.stream().map(ExpressionNodes::getNode).collect(ImmutableList.collector(expressions.size()));
    }

    /// 连接节点数组和表达式列表。
    ///
    /// @param nodes       现有节点数组
    /// @param expressions 表达式列表
    /// @return 连接后的节点不可变列表
    public static ImmutableList<ExpressionNode> join(ImmutableArray<ExpressionNode> nodes, List<? extends Expression<?, ?>> expressions) {
        return ImmutableList.concat(nodes.asList(), getNode(expressions));
    }

    /// 连接节点数组和路径引用。
    ///
    /// @param <T>   实体类型
    /// @param nodes 现有节点数组
    /// @param path  路径引用
    /// @return 连接后的节点不可变列表
    public static <T> ImmutableList<ExpressionNode> join(ImmutableArray<ExpressionNode> nodes, PathRef<T, ?> path) {
        ImmutableList.Builder<ExpressionNode> builder = new ImmutableList.Builder<>(nodes.size() + 1);
        builder.addAll(nodes);
        builder.add(PathNode.of(path));
        return builder.build();
    }

    /// 连接节点数组和路径引用集合。
    ///
    /// @param <T>   实体类型
    /// @param nodes 现有节点数组
    /// @param paths 路径引用集合
    /// @return 连接后的节点不可变列表
    public static <T> ImmutableList<ExpressionNode> join(ImmutableArray<ExpressionNode> nodes, Collection<PathRef<T, ?>> paths) {
        ImmutableList.Builder<ExpressionNode> builder = new ImmutableList.Builder<>(nodes.size() + paths.size());
        builder.addAll(nodes);
        for (PathRef<T, ?> path : paths) {
            builder.add(PathNode.of(path));
        }
        return builder.build();
    }

    /// 将表达式集合映射为节点列表。
    ///
    /// @param <T>         实体类型
    /// @param expressions 表达式集合
    /// @return 节点的不可变列表
    public static <T> ImmutableList<ExpressionNode> mapping(Collection<? extends Expression<T, ?>> expressions) {
        return expressions.stream().map(ExpressionNodes::getNode).collect(ImmutableList.collector(expressions.size()));
    }

    /// 将表达式数组映射为节点列表。
    ///
    /// @param <T>         实体类型
    /// @param expressions 表达式数组
    /// @return 节点的不可变列表
    public static <T> ImmutableList<ExpressionNode> mapping(Expression<T, ?>[] expressions) {
        return Arrays.stream(expressions).map(ExpressionNodes::getNode).collect(ImmutableList.collector(expressions.length));
    }

}
