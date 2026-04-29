package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.MetamodelAttribute;
import io.github.nextentity.core.meta.MetamodelSchema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.Lazy;

import java.util.Map;

public abstract class AbstractMetamodelSchema<A extends MetamodelAttribute> implements MetamodelSchema<A> {

    protected final Class<?> type;

    protected final Lazy<AttributeSet<A>> attributesSupplier = new Lazy<>(this::createAttributes);

    public AbstractMetamodelSchema(Class<?> type) {
        this.type = type;
    }

    abstract protected AttributeSet<A> createAttributes();

    @Override
    public ImmutableArray<? extends A> getAttributes() {
        return attributesSupplier.get().attributes();
    }

    @Override
    public ImmutableArray<? extends A> getPrimitives() {
        return attributesSupplier.get().primitives();
    }

    @Override
    public A getAttribute(String name) {
        Map<String, A> index = attributesSupplier.get().index();
        return index.get(name);
    }

    @Override
    public A getAttribute(Iterable<String> fieldNames) {
        MetamodelSchema<?> schema = this;
        java.util.Iterator<String> it = fieldNames.iterator();
        while (it.hasNext()) {
            String fieldName = it.next();
            MetamodelAttribute attr = schema.getAttribute(fieldName);
            if (it.hasNext()) {
                schema = (MetamodelSchema<?>) attr;
            } else {
                //noinspection unchecked
                return (A) attr;
            }
        }
        //noinspection unchecked
        return (A) schema;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    protected Lazy<? extends AttributeSet<A>> getAttributesSupplier() {
        return attributesSupplier;
    }

    @Override
    public String toString() {
        if (this instanceof MetamodelAttribute attribute) {
            return attribute.path().toString();
        }
        return getClass().toString();
    }
}
