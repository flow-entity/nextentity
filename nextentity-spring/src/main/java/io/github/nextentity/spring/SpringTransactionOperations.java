package io.github.nextentity.spring;

import io.github.nextentity.core.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

public class SpringTransactionOperations implements TransactionOperations {

    private final TransactionTemplate template;

    public SpringTransactionOperations(TransactionTemplate template) {
        this.template = template;
    }

    @Override
    public <T> T executeInTransaction(Supplier<T> operation) {
        return template.execute(_ -> operation.get());
    }

}
