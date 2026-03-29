package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.SchemaAttribute;

/**
 * Join attribute interface representing an association between entities.
 * <p>
 * This interface extends both {@link SchemaAttribute} and {@link EntitySchema}
 * to provide metadata about entity relationships, including join table details
 * and foreign key references.
 * <p>
 * Join attributes are used to build JOIN queries and understand entity
 * relationships in the metamodel.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface JoinAttribute extends SchemaAttribute, Attribute, EntitySchema {

    /**
     * Gets the join table name for this association.
     * <p>
     * For many-to-many relationships, this returns the intermediate join table.
     * For other relationships, this returns the target entity's table name.
     *
     * @return the join table name
     */
    String tableName();

    /**
     * Gets the name of this join attribute in the declaring entity.
     *
     * @return the join attribute name
     */
    String joinName();

    /**
     * Gets the foreign key column name that references the target entity.
     *
     * @return the referenced column name
     */
    String referencedColumnName();

}
