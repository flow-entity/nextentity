package io.github.nextentity.core.meta;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Attributes;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;

/**
 * Entity schema interface providing metadata about entity structure.
 * <p>
 * This interface extends {@link Schema} with entity-specific metadata such as
 * table name, primary key, and version attribute for optimistic locking.
 * <p>
 * Entity schemas are used by the query builder and persistence operations
 * to understand entity structure and mapping to database tables.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface EntitySchema extends Schema {

    /**
     * Gets the primary key (identity) attribute of this entity.
     *
     * @return the identity attribute
     */
    EntityAttribute id();

    /**
     * Gets the database table name for this entity.
     *
     * @return the table name
     */
    String tableName();

    /**
     * Gets the primitive (non-association) attributes of this entity.
     * <p>
     * This method overrides the parent schema method to return
     * {@link EntityAttribute} instances instead of generic attributes.
     *
     * @return an immutable array of primitive entity attributes
     */
    @Override
    default ImmutableArray<? extends EntityAttribute> getPrimitives() {
        ImmutableArray<? extends Attribute> attributes = Schema.super.getPrimitives();
        return TypeCastUtil.cast(attributes);
    }

    /**
     * Gets the version attribute for optimistic locking.
     * <p>
     * Returns null if this entity does not use optimistic locking.
     *
     * @return the version attribute, or null if not applicable
     */
    EntityAttribute version();

    /**
     * Gets all attributes of this entity, including associations.
     *
     * @return the attributes collection
     */
    @Override
    Attributes attributes();
}
