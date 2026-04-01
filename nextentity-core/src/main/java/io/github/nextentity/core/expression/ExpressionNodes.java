package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.Path;
import io.github.nextentity.api.PathRef;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ExpressionNodes {

    public static boolean isNullOrTrue(Expression<?, ?> expression) {
        if (expression == null) {
            return true;
        }
        ExpressionNode node = getNode(expression);
        return isNullOrTrue(node);
    }

    public static boolean isNullOrTrue(ExpressionNode node) {
        return node == null || node == EmptyNode.INSTANCE
               || (node instanceof LiteralNode literalNode) && Boolean.TRUE.equals(literalNode.value());
    }

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

    public static ImmutableList<ExpressionNode> getNode(List<? extends Expression<?, ?>> expressions) {
        return expressions.stream().map(ExpressionNodes::getNode).collect(ImmutableList.collector(expressions.size()));
    }

    public static ImmutableList<ExpressionNode> join(ImmutableArray<ExpressionNode> nodes, List<? extends Expression<?, ?>> expressions) {
        return ImmutableList.concat(nodes.asList(), getNode(expressions));
    }

    public static <T> ImmutableList<ExpressionNode> join(ImmutableArray<ExpressionNode> nodes, PathRef<T, ?> path) {
        ImmutableList.Builder<ExpressionNode> builder = new ImmutableList.Builder<>(nodes.size() + 1);
        builder.addAll(nodes);
        builder.add(PathNode.of(path));
        return builder.build();
    }

    public static <T> ImmutableList<ExpressionNode> join(ImmutableArray<ExpressionNode> nodes, Collection<PathRef<T, ?>> paths) {
        ImmutableList.Builder<ExpressionNode> builder = new ImmutableList.Builder<>(nodes.size() + paths.size());
        builder.addAll(nodes);
        for (PathRef<T, ?> path : paths) {
            builder.add(PathNode.of(path));
        }
        return builder.build();
    }

    public static <T> ImmutableList<ExpressionNode> mapping(Collection<? extends Expression<T, ?>> expressions) {
        return expressions.stream().map(ExpressionNodes::getNode).collect(ImmutableList.collector(expressions.size()));
    }

    public static <T> ImmutableList<ExpressionNode> mapping(Expression<T, ?>[] expressions) {
        return Arrays.stream(expressions).map(ExpressionNodes::getNode).collect(ImmutableList.collector(expressions.length));
    }

}
