package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.expression.InternalPathExpression;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.expression.QueryStructure.From;
import io.github.nextentity.core.expression.QueryStructure.From.FromEntity;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.ImmutableArray;

import java.util.Collection;

/**
 * @author HuangChengwei
 * @since 2024/4/20 下午12:03
 */
public class QueryContext {

    protected final QueryStructure structure;
    protected final Metamodel metamodel;
    protected final EntityType entityType;
    protected final boolean expandReferencePath;
    private final SelectedContext selectedContext;

    public QueryContext(QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute) {
        this.structure = structure;
        this.metamodel = metamodel;
        this.expandReferencePath = expandObjectAttribute;
        From from = structure.from();
        this.entityType = from instanceof FromEntity ? metamodel.getEntity(from.type()) : null;
        this.selectedContext = selectedContext();
    }

    public QueryContext newContext(QueryStructure structure) {
        return new QueryContext(structure, metamodel, expandReferencePath);
    }

    public ImmutableArray<Expression> getSelectedExpression() {
        return selectedContext.expressions();
    }

    private SelectedContext selectedContext() {
        QueryStructure.Selected select = structure.select();
        if (select instanceof QueryStructure.Selected.SelectEntity) {
            Collection<? extends InternalPathExpression> fetch = ((QueryStructure.Selected.SelectEntity) select).fetch();
            SchemaAttributePaths schemaAttributePaths = SchemaAttributePaths.empty();
            if (fetch != null && !fetch.isEmpty() && expandReferencePath) {
                schemaAttributePaths = newJoinPaths(fetch);
            }
            return new SelectEntityContext(entityType, schemaAttributePaths);
        } else if (select instanceof QueryStructure.Selected.SelectProjection selectProjection) {
            return new SelectProjectionContext(entityType.getProjection(selectProjection.type()));
        } else if (select instanceof QueryStructure.Selected.SelectPrimitive selectPrimitive) {
            return new SelectPrimitiveContext(entityType, selectPrimitive.expression());
        } else if (select instanceof QueryStructure.Selected.SelectArray selectArray) {
            return new SelectArrayContext(entityType, selectArray);
        }
        throw new IllegalArgumentException("Unknown select type: " + select.getClass().getName());
    }

    private SchemaAttributePaths newJoinPaths(Collection<? extends InternalPathExpression> fetch) {
        DefaultSchemaAttributePaths paths = new DefaultSchemaAttributePaths();
        for (InternalPathExpression strings : fetch) {
            paths.add(strings);
        }
        return paths;
    }


    public Object construct(Arguments arguments) {
        return selectedContext.construct(arguments);
    }

    public QueryStructure getStructure() {
        return this.structure;
    }

    public Metamodel getMetamodel() {
        return this.metamodel;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

}
