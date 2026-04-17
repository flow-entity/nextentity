package io.github.nextentity.jdbc;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.expression.SelectProjection;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.meta.ProjectionSchema;
import io.github.nextentity.core.util.ImmutableArray;

public class SelectProjectionContext extends QueryContext {
    private final ProjectionSchema projection;
    private final ImmutableArray<SelectItem> expressions;
    private final SchemaAttributePaths schemaAttributePaths;

    public SelectProjectionContext(QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute, SelectProjection select) {
        super(structure, metamodel, expandObjectAttribute);
        this.projection = entityType.getProjection(select.type());
        this.schemaAttributePaths = DeepLimitSchemaAttributePaths.of(1);
        this.expressions = getSelectSchemaExpressions(projection, schemaAttributePaths);
    }

    @Override
    public ImmutableArray<SelectItem> getSelectedExpression() {
        return expressions;
    }

    @Override
    public Object construct(Arguments arguments) {
        return constructSchema(projection, arguments, schemaAttributePaths);
    }
}
