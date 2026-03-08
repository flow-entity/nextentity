package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.AbstractSchemaAttribute;
import io.github.nextentity.core.reflect.schema.Attributes;

import java.util.function.Function;

public class SimpleProjectionJoinAttribute extends AbstractSchemaAttribute implements ProjectionJoinAttribute {

    private final JoinAttribute source;
    private final Function<? super SimpleProjectionJoinAttribute, Attributes> attributeBuilder;

    public SimpleProjectionJoinAttribute(JoinAttribute source, Function<? super SimpleProjectionJoinAttribute, Attributes> attributeBuilder) {
        this.source = source;
        this.attributeBuilder = attributeBuilder;
    }

    @Override
    public JoinAttribute source() {
        return source;
    }

    @Override
    protected Attributes buildAttributes() {
        return attributeBuilder.apply(this);
    }


}
