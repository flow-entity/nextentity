package io.github.nextentity.core.meta;

import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.reflect.schema.SimpleAttribute;

public class SimpleEntityAttribute extends SimpleAttribute implements EntityAttribute {
    private String columnName;
    private ValueConvertor<?, ?> valueConvertor;
    private boolean version;
    private boolean id;
    private boolean updatable;
    private volatile PathNode pathNode;

    @Override
    public String columnName() {
        return columnName;
    }

    @Override
    public ValueConvertor<?, ?> valueConvertor() {
        return valueConvertor;
    }

    @Override
    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
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

    public void setValueConvertor(ValueConvertor<?, ?> valueConvertor) {
        this.valueConvertor = valueConvertor;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.join(".", path());
    }

    @Override
    public PathNode expression() {
        if (pathNode == null) {
            synchronized (this) {
                if (pathNode == null) {
                    pathNode = new PathNode(path().toArray(String[]::new));
                }
            }
        }
        return pathNode;
    }
}
