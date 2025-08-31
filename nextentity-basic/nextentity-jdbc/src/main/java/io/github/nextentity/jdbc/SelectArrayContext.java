package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.Tuples;
import io.github.nextentity.core.expression.InternalPathExpression;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

public class SelectArrayContext implements SelectedContext {

    private final EntityType entityType;
    private final ImmutableArray<Expression> expressions;
    private final ImmutableArray<Object> selectExpressions;

    public SelectArrayContext(EntityType entityType, QueryStructure.Selected.SelectArray selectArray) {
        this.entityType = entityType;
        this.selectExpressions = selectArray.items().stream()
                .map(QueryStructure.Selected.SelectPrimitive::expression)
                .map(it -> it instanceof InternalPathExpression pathExpression
                        ? entityType.getAttribute(pathExpression) : it)
                .collect(ImmutableList.collector(selectArray.items().size()));
        this.expressions = selectArray.items().stream()
                .flatMap(e -> SelectedContext.stream(entityType, e.expression(), DeepLimitSchemaAttributePaths.of(0)))
                .collect(ImmutableList.collector());
    }

    @Override
    public ImmutableArray<Expression> expressions() {
        return expressions;
    }

    @Override
    public Object construct(Arguments arguments) {
        Object[] objects = new Object[selectExpressions.size()];
        for (int i = 0; i < objects.length; i++) {
            Object expression = selectExpressions.get(i);
            objects[i] = SelectedContext.constructExpression(entityType, arguments, expression);
        }
        return Tuples.of(objects);
    }

}
