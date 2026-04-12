package io.github.nextentity.core;

import io.github.nextentity.api.EntityDescriptor;
import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.UpdateStructure;
import org.jspecify.annotations.NonNull;

public class TransactionUpdateExecutor implements PersistExecutor {

    private final PersistExecutor target;
    private final TransactionOperations transaction;

    public TransactionUpdateExecutor(PersistExecutor target, TransactionOperations transaction) {
        this.target = target;
        this.transaction = transaction;
    }

    @Override
    public <T> void insertAll(@NonNull Iterable<T> entities, @NonNull EntityDescriptor<T> descriptor) {
        transaction.executeInTransaction(() -> target.insertAll(entities, descriptor));
    }

    @Override
    public <T> void updateAll(@NonNull Iterable<T> entities, @NonNull EntityDescriptor<T> descriptor) {
        transaction.executeInTransaction(() -> target.updateAll(entities, descriptor));
    }

    @Override
    public <T> void deleteAll(@NonNull Iterable<T> entities, @NonNull EntityDescriptor<T> descriptor) {
        transaction.executeInTransaction(() -> target.deleteAll(entities, descriptor));
    }

    @Override
    public <T> int update(UpdateStructure structure, @NonNull EntityDescriptor<T> descriptor) {
        return transaction.executeInTransaction(() -> target.update(structure, descriptor));
    }

    @Override
    public <T> int delete(ExpressionNode predicate, @NonNull EntityDescriptor<T> descriptor) {
        return transaction.executeInTransaction(() -> target.delete(predicate, descriptor));
    }

}
