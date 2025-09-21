package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.util.ImmutableArray;

import java.util.Collection;

public class SelectEntityContext extends QueryContext {

    private final SchemaAttributePaths schemaAttributePaths;
    private final ImmutableArray<SelectItem> expressions;

    public SelectEntityContext(QueryStructure structure, Metamodel metamodel, Collection<? extends Attribute> fetch) {
        super(structure, metamodel, true);
        this.schemaAttributePaths = newJoinPaths(fetch);
        this.expressions = getSelectSchemaExpressions(entityType, schemaAttributePaths);
    }


    @Override
    public ImmutableArray<SelectItem> getSelectedExpression() {
        return expressions;
    }

    @Override
    public Object construct(Arguments arguments) {
        return constructSchema(entityType, arguments, schemaAttributePaths);
    }
}
