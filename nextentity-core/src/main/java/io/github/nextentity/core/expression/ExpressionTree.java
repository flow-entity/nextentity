package io.github.nextentity.core.expression;

/**
 * Interface providing access to the root node of an expression tree.
 * <p>
 * Implemented by expression builders and typed expressions to expose
 * the underlying expression tree structure for query building.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface ExpressionTree {

    /**
     * Gets the root node of this expression tree.
     *
     * @return the root expression node
     */
    ExpressionNode getRoot();
}
