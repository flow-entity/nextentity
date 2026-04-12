package io.github.nextentity.core;

import io.github.nextentity.api.*;
import io.github.nextentity.core.expression.*;

public class UpdateSetStepImpl<T> implements UpdateSetStep<T> {
    private final UpdateStructure structure;
    private final PersistDescriptor<T> descriptor;

    public UpdateSetStepImpl(PersistDescriptor<T> descriptor) {
        this(descriptor, UpdateStructure.EMPTY);
    }

    public UpdateSetStepImpl(PersistDescriptor<T> descriptor, UpdateStructure structure) {
        this.descriptor = descriptor;
        this.structure = structure;
    }

    @Override
    public <U> UpdateSetStep<T> set(PathRef<T, U> path, U value) {
        PathReference reference = PathReference.of(path);
        String fieldName = reference.getFieldName();
        UpdateStructure updated = structure.addSetClause(fieldName, value);
        return new UpdateSetStepImpl<>(descriptor, updated);
    }

    @Override
    public int execute() {
        return descriptor.persistExecutor().update(structure, descriptor);
    }

    @Override
    public UpdateWhereStep<T> where(Expression<T, Boolean> predicate) {
        if (ExpressionNodes.isNullOrTrue(predicate)) {
            return this;
        }
        ExpressionNode node = ExpressionNodes.getNode(predicate);
        return applyWhere(node);
    }

    private UpdateSetStepImpl<T> applyWhere(ExpressionNode node) {
        ExpressionNode operate = structure.where().operate(Operator.AND, node);
        UpdateStructure updated = structure.where(operate);
        return new UpdateSetStepImpl<>(descriptor, updated);
    }

    @Override
    public <N> ExpressionBuilder.PathOperator<T, N, ? extends UpdateWhereStep<T>> where(PathRef<T, N> path) {
        return new PathOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, ? extends UpdateWhereStep<T>> where(PathRef.NumberRef<T, N> path) {
        return new NumberOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public ExpressionBuilder.StringOperator<T, ? extends UpdateWhereStep<T>> where(PathRef.StringRef<T> path) {
        return new StringOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public <N> ExpressionBuilder.PathOperator<T, N, ? extends UpdateWhereStep<T>> where(Path<T, N> path) {
        return new PathOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, ? extends UpdateWhereStep<T>> where(NumberPath<T, N> path) {
        return new NumberOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public ExpressionBuilder.StringOperator<T, ? extends UpdateWhereStep<T>> where(StringPath<T> path) {
        return new StringOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }

    @Override
    public <R extends Entity> ExpressionBuilder.PathOperator<T, R, ? extends UpdateWhereStep<T>> where(PathRef.EntityPathRef<T, R> path) {
        return new PathOperatorImpl<>(PathNode.of(path), this::applyWhere);
    }
}
