package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.expression.InternalPathExpression;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.ImmutableArray;

import java.util.Collection;

public class SelectEntityContext extends QueryContext {

    private final SchemaAttributePaths schemaAttributePaths;
    private final ImmutableArray<Expression> expressions;

    public SelectEntityContext(QueryStructure structure, Metamodel metamodel, Collection<? extends InternalPathExpression> fetch) {
        super(structure, metamodel, true);
        this.schemaAttributePaths = newJoinPaths(fetch);
        this.expressions = getSelectSchemaExpressions(entityType, schemaAttributePaths);
    }


    @Override
    public ImmutableArray<Expression> getSelectedExpression() {
        return expressions;
    }

    @Override
    public Object construct(Arguments arguments) {
        return constructSchema(entityType, arguments, schemaAttributePaths);
    }
}
