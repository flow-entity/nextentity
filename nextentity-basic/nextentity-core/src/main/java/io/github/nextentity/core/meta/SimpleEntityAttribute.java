package io.github.nextentity.core.meta;

import io.github.nextentity.core.expression.InternalPathExpression;
import io.github.nextentity.core.expression.impl.AbstractInternalPathExpression;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.reflect.schema.SimpleAttribute;

import java.util.List;

public class SimpleEntityAttribute extends SimpleAttribute implements EntityAttribute, AbstractInternalPathExpression {
    private String columnName;
    private ValueConvertor valueConvertor;
    private boolean version;
    private boolean id;
    private String[] paths;

    @Override
    public String columnName() {
        return columnName;
    }

    @Override
    public ValueConvertor valueConvertor() {
        return valueConvertor;
    }

    @Override
    public boolean isVersion() {
        return version;
    }

    @Override
    public boolean isId() {
        return id;
    }

    @Override
    public Schema declareBy() {
        return super.declareBy();
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setValueConvertor(ValueConvertor valueConvertor) {
        this.valueConvertor = valueConvertor;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    @Override
    public String[] paths() {
        return paths;
    }

    @Override
    public int deep() {
        return paths.length;
    }

    @Override
    public InternalPathExpression path() {
        return this;
    }

    @Override
    public void setAttribute(Attribute attribute) {
        super.setAttribute(attribute);
        List<? extends Attribute> list = attributePaths();
        this.paths = list.stream().map(Attribute::name).toArray(String[]::new);
    }

    @Override
    public String toString() {
        return String.join(".", paths);
    }
}
