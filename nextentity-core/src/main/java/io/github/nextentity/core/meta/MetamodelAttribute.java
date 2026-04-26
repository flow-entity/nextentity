package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;

public sealed interface MetamodelAttribute extends Attribute permits EntityAttribute, JoinAttribute, ProjectionAttribute {
}
