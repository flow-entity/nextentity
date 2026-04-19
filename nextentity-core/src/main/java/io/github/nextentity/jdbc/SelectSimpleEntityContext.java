package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import org.jspecify.annotations.Nullable;

public class SelectSimpleEntityContext extends QueryContext {

    protected SelectSimpleEntityContext(QueryExecutor executor, QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute) {
        super(executor, structure, metamodel, expandObjectAttribute);
    }

    @Override
    public ImmutableArray<SelectItem> getSelectedExpression() {
        return TypeCastUtil.cast(entityType.getPrimitives());
    }

    /// 获取当前构造的 Schema
    ///
    /// @return 实体 Schema
    @Override
    @Nullable
    public Schema getSchema() {
        return entityType;
    }

    @Override
    public Object construct(Arguments arguments) {
        return constructSimpleSchema(entityType, arguments);
    }

}
