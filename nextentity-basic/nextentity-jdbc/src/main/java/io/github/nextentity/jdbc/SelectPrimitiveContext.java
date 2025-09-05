package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.expression.QueryStructure.Selected.SelectPrimitive;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.ImmutableArray;

public class SelectPrimitiveContext extends QueryContext {

    private final ImmutableArray<Expression> expressions;
    private final Expression expression;


    protected SelectPrimitiveContext(QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute, SelectPrimitive selectPrimitive) {
        super(structure, metamodel, expandObjectAttribute);
        this.expression = selectPrimitive.expression();
        this.expressions = getSelectPrimitiveExpressions(entityType, expression, DeepLimitSchemaAttributePaths.of(0));
    }


    @Override
    public ImmutableArray<Expression> getSelectedExpression() {
        return expressions;
    }

    @Override
    public Object construct(Arguments arguments) {
        return constructExpression(entityType, arguments, expression);
    }
}
