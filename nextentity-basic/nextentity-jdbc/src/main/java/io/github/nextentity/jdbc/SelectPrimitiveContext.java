package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.util.ImmutableArray;

public class SelectPrimitiveContext implements SelectedContext {

    private final ImmutableArray<Expression> expressions;
    private final EntityType entityType;
    private final Expression expression;

    protected SelectPrimitiveContext(EntityType entityType, Expression expression) {
        this.entityType = entityType;
        this.expression = expression;
        this.expressions = SelectedContext.getSelectPrimitiveExpressions(entityType, expression, DeepLimitSchemaAttributePaths.of(0));
    }


    @Override
    public ImmutableArray<Expression> expressions() {
        return expressions;
    }

    @Override
    public Object construct(Arguments arguments) {
        return SelectedContext.constructExpression(entityType, arguments, expression);
    }
}
