package io.github.nextentity.core.reflect.schema;

public class SimpleSchema implements Schema {

    private Class<?> type;
    private Attributes attributes;

    public Class<?> type() {
        return this.type;
    }

    public Attributes attributes() {
        return this.attributes;
    }

    public SimpleSchema type(Class<?> type) {
        this.type = type;
        return this;
    }

    public SimpleSchema attributes(Attributes attributes) {
        this.attributes = attributes;
        return this;
    }
}
