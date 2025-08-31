package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.util.ImmutableArray;

public class SelectEntityContext implements SelectedContext {

    private final EntityType entityType;
    private final SchemaAttributePaths schemaAttributePaths;
    private final ImmutableArray<Expression> expressions;

    public SelectEntityContext(EntityType entityType, SchemaAttributePaths schemaAttributePaths) {
        this.entityType = entityType;
        this.schemaAttributePaths = schemaAttributePaths;
        if (schemaAttributePaths.isEmpty()) {
            this.expressions = TypeCastUtil.cast(entityType.primitiveAttributes());
        } else {
            this.expressions = SelectedContext.getSelectSchemaExpressions(entityType, schemaAttributePaths);
        }
    }

    @Override
    public ImmutableArray<Expression> expressions() {
        return expressions;
    }

    @Override
    public Object construct(Arguments arguments) {
        return SelectedContext.constructSchema(entityType, arguments, schemaAttributePaths);
    }
}
