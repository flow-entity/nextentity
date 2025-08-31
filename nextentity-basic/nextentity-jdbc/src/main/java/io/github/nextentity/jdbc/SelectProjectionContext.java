package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.meta.ProjectionType;
import io.github.nextentity.core.util.ImmutableArray;

public class SelectProjectionContext implements SelectedContext {
    private final ProjectionType projection;
    private final ImmutableArray<Expression> expressions;
    private final SchemaAttributePaths schemaAttributePaths;

    public SelectProjectionContext(ProjectionType projection) {
        this.projection = projection;
        this.schemaAttributePaths = DeepLimitSchemaAttributePaths.of(1);
        this.expressions = SelectedContext.getSelectSchemaExpressions(projection, schemaAttributePaths);
    }

    @Override
    public ImmutableArray<Expression> expressions() {
        return expressions;
    }

    @Override
    public Object construct(Arguments arguments) {
        return SelectedContext.constructSchema(projection, arguments, schemaAttributePaths);
    }
}
