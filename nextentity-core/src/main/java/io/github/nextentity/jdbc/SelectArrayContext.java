package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.Tuples;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.expression.SelectExpressions;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

public class SelectArrayContext extends QueryContext {

    private final ImmutableArray<io.github.nextentity.core.SelectItem> expressions;
    private final ImmutableArray<Object> selectExpressions;

    public SelectArrayContext(QueryExecutor executor, QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute, SelectExpressions selectArray) {
        super(executor, structure, metamodel, expandObjectAttribute);
        this.selectExpressions = selectArray.items().stream()
                .map(it -> it instanceof PathNode pathExpression
                        ? entityType.getAttribute(pathExpression) : it)
                .collect(ImmutableList.collector(selectArray.items().size()));
        this.expressions = selectArray.items().stream()
                .flatMap(e -> stream(entityType, e, DeepLimitSchemaAttributePaths.of(0)))
                .collect(ImmutableList.collector());
    }

    @Override
    public ImmutableArray<io.github.nextentity.core.SelectItem> getSelectedExpression() {
        return expressions;
    }

    @Override
    public Object construct(Arguments arguments) {
        Object[] objects = new Object[selectExpressions.size()];
        for (int i = 0; i < objects.length; i++) {
            Object expression = selectExpressions.get(i);
            objects[i] = constructExpression(entityType, arguments, expression);
        }
        return Tuples.of(objects);
    }

}
