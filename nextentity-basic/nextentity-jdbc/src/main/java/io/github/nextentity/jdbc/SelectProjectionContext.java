package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.expression.QueryStructure.Selected.SelectProjection;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.meta.ProjectionType;
import io.github.nextentity.core.util.ImmutableArray;

public class SelectProjectionContext extends QueryContext {
    private final ProjectionType projection;
    private final ImmutableArray<Expression> expressions;
    private final SchemaAttributePaths schemaAttributePaths;

    public SelectProjectionContext(QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute, SelectProjection select) {
        super(structure, metamodel, expandObjectAttribute);
        this.projection = entityType.getProjection(select.type());
        this.schemaAttributePaths = DeepLimitSchemaAttributePaths.of(1);
        this.expressions = getSelectSchemaExpressions(projection, schemaAttributePaths);
    }

    @Override
    public ImmutableArray<Expression> getSelectedExpression() {
        return expressions;
    }

    @Override
    public Object construct(Arguments arguments) {
        return constructSchema(projection, arguments, schemaAttributePaths);
    }
}
