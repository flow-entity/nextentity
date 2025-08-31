package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.AbstractSchemaAttribute;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Attributes;

import java.util.function.Function;

public class SimpleJoinAttribute extends AbstractSchemaAttribute implements JoinAttribute {

    private final Function<JoinAttribute, Attributes> attributesFunction;
    private String joinName;
    private String referencedColumnName;
    private EntityAttribute id;
    private EntityAttribute version;
    private String tableName;

    public SimpleJoinAttribute(Function<JoinAttribute, Attributes> attributesFunction) {
        this.attributesFunction = attributesFunction;
    }

    @Override
    public EntityAttribute id() {
        return id;
    }

    @Override
    public EntityAttribute version() {
        return version;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public String joinName() {
        return joinName;
    }

    @Override
    public String referencedColumnName() {
        return referencedColumnName;
    }

    @Override
    protected Attributes buildAttributes() {
        Attributes attributes = attributesFunction.apply(this);
        EntityAttribute version = null;
        EntityAttribute id = null;
        for (Attribute attribute : attributes) {
            if (attribute instanceof EntityAttribute column) {
                if (column.isId()) {
                    id = column;
                } else if (column.isVersion()) {
                    version = column;
                }
            }
        }
        this.id = id;
        this.version = version;
        return attributes;
    }

    public void setJoinName(String joinName) {
        this.joinName = joinName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
