package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.ImmutableArray;

public class SelectSimpleEntityContext extends QueryContext {

    protected SelectSimpleEntityContext(QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute) {
        super(structure, metamodel, expandObjectAttribute);
    }

    @Override
    public ImmutableArray<SelectItem> getSelectedExpression() {
        return TypeCastUtil.cast(entityType.getPrimitives());
    }

    @Override
    public Object construct(Arguments arguments) {
        return constructSimpleSchema(entityType, arguments);
    }

}
