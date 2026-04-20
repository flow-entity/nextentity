package io.github.nextentity.jdbc;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.expression.SelectEntity;
import io.github.nextentity.core.expression.Selected;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;
import java.util.stream.Collectors;

public class SelectEntityContext extends QueryContext {

    private SchemaAttributePaths schemaAttributePaths;
    private ImmutableArray<SelectItem> expressions;

    public SelectEntityContext() {
    }

    @Override
    public void init() {
        super.init();
        Selected select = structure.select();
        if (select instanceof SelectEntity selectEntity) {
            ImmutableList<PathNode> fetchNodes = selectEntity.fetch();
            if (fetchNodes != null && !fetchNodes.isEmpty() && expandReferencePath) {
                Collection<? extends Attribute> fetch = fetchNodes
                        .stream()
                        .map(it -> it.getAttribute(entityType))
                        .collect(Collectors.toList());
                this.schemaAttributePaths = newJoinPaths(fetch);
                this.expressions = getSelectSchemaExpressions(entityType, schemaAttributePaths);
            } else {
                this.schemaAttributePaths = SchemaAttributePaths.empty();
                this.expressions = TypeCastUtil.cast(entityType.getPrimitives());
            }
        }
    }

    @Override
    public ImmutableArray<SelectItem> getSelectedExpression() {
        return expressions;
    }

    @Override
    public Object doConstruct(Arguments arguments) {
        if (schemaAttributePaths.isEmpty()) {
            return constructSimpleSchema(entityType, arguments);
        } else {
            return constructSchema(entityType, arguments, schemaAttributePaths);
        }
    }
}