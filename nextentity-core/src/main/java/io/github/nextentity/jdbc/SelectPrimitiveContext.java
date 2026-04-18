package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.expression.SelectExpression;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.ImmutableArray;

public class SelectPrimitiveContext extends QueryContext {

    private final ImmutableArray<SelectItem> expressions;
    private final ExpressionNode expression;


    protected SelectPrimitiveContext(QueryExecutor executor, QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute, SelectExpression selectPrimitive) {
        super(executor, structure, metamodel, expandObjectAttribute);
        this.expression = selectPrimitive.expression();
        this.expressions = getSelectPrimitiveExpressions(entityType, expression, DeepLimitSchemaAttributePaths.of(0));
    }


    @Override
    public ImmutableArray<SelectItem> getSelectedExpression() {
        return expressions;
    }

    @Override
    public Object construct(Arguments arguments) {
        return constructExpression(entityType, arguments, expression);
    }
}
