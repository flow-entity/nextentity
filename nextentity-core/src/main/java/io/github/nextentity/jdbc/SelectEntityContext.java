package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

public class SelectEntityContext extends QueryContext {

    private final SchemaAttributePaths schemaAttributePaths;
    private final ImmutableArray<SelectItem> expressions;

    public SelectEntityContext(QueryExecutor executor, QueryStructure structure, Metamodel metamodel, Collection<? extends Attribute> fetch) {
        super(executor, structure, metamodel, true);
        this.schemaAttributePaths = newJoinPaths(fetch);
        this.expressions = getSelectSchemaExpressions(entityType, schemaAttributePaths);
    }


    @Override
    public ImmutableArray<SelectItem> getSelectedExpression() {
        return expressions;
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
    public Object doConstruct(Arguments arguments) {
        return constructSchema(entityType, arguments, schemaAttributePaths);
    }
}
