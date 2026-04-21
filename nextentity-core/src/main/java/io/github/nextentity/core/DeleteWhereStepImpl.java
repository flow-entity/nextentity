package io.github.nextentity.core;

import io.github.nextentity.api.*;
import io.github.nextentity.core.expression.*;

public class DeleteWhereStepImpl<T> implements DeleteWhereStep<T> {

    private final ExpressionNode predicate;
    private final PersistDescriptor<T> descriptor;

    public DeleteWhereStepImpl(PersistDescriptor<T> descriptor) {
        this(descriptor, EmptyNode.INSTANCE);
    }

    public DeleteWhereStepImpl(PersistDescriptor<T> descriptor, ExpressionNode predicate) {
        this.predicate = predicate;
        this.descriptor = descriptor;
    }

    @Override
    public int execute() {
        return persistExecutor().delete(predicate, descriptor);
    }

    private PersistExecutor persistExecutor() {
        return descriptor.persistConfig().persistExecutor();
    }

    @Override
    public DeleteWhereStep<T> where(Expression<T, Boolean> predicate) {
        if (ExpressionNodes.isNullOrTrue(predicate)) {
            return this;
        }
        ExpressionNode node = ExpressionNodes.getNode(predicate);
        return applyWhere(node);
    }


    private DeleteWhereStep<T> applyWhere(ExpressionNode node) {
        ExpressionNode updated = predicate.operate(Operator.AND, node);
        return new DeleteWhereStepImpl<>(descriptor, updated);
    }

    @Override
    public <N> ExpressionBuilder.PathOperator<T, N, ? extends DeleteWhereStep<T>> where(PathRef<T, N> path) {
        return new PathOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, ? extends DeleteWhereStep<T>> where(PathRef.NumberRef<T, N> path) {
        return new NumberOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public ExpressionBuilder.StringOperator<T, ? extends DeleteWhereStep<T>> where(PathRef.StringRef<T> path) {
        return new StringOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public <N> ExpressionBuilder.PathOperator<T, N, ? extends DeleteWhereStep<T>> where(Path<T, N> path) {
        return new PathOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, ? extends DeleteWhereStep<T>> where(NumberPath<T, N> path) {
        return new NumberOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public ExpressionBuilder.StringOperator<T, ? extends DeleteWhereStep<T>> where(StringPath<T> path) {
        return new StringOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public <R extends Entity> ExpressionBuilder.PathOperator<T, R, ? extends DeleteWhereStep<T>> where(PathRef.EntityPathRef<T, R> path) {
        return new PathOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }
}
