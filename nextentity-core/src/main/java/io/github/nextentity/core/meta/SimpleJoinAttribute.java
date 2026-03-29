package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.AbstractSchemaAttribute;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Attributes;

import java.util.function.Function;

///
/// Simple implementation of {@link JoinAttribute}.
///
/// This class provides a concrete implementation for join/association attributes
/// with lazy attribute building via a supplied function.
///
/// Join attributes represent entity associations (Many-to-One, One-to-One) and
/// contain metadata about join columns and referenced tables.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class SimpleJoinAttribute extends AbstractSchemaAttribute implements JoinAttribute {

    private final Function<JoinAttribute, Attributes> attributesFunction;
    private String joinName;
    private String referencedColumnName;
    private EntityAttribute id;
    private EntityAttribute version;
    private String tableName;

    ///
    /// Creates a new SimpleJoinAttribute instance.
    ///
    /// @param attributesFunction function to build target entity attributes
    ///
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

    ///
    /// Builds the target entity attributes and extracts ID and version.
    ///
    /// @return the target entity attributes
    ///
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

    ///
    /// Sets the join column name.
    ///
    /// @param joinName the join column name
    ///
    public void setJoinName(String joinName) {
        this.joinName = joinName;
    }

    ///
    /// Sets the referenced (foreign key) column name.
    ///
    /// @param referencedColumnName the referenced column name
    ///
    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

    ///
    /// Sets the target entity table name.
    ///
    /// @param tableName the table name
    ///
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
