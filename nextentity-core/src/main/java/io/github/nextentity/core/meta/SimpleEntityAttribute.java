package io.github.nextentity.core.meta;

import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.reflect.schema.SimpleAttribute;

/// {@link EntityAttribute} 的简单实现。
///
/// 此类为实体属性提供了具体实现，
/// 支持列映射、值转换、版本控制和标识。
public class SimpleEntityAttribute extends SimpleAttribute implements EntityAttribute {
    private String columnName;
    private ValueConverter<?, ?> valueConvertor;
    private boolean version;
    private boolean id;
    private boolean updatable;
    private volatile PathNode pathNode;

    @Override
    public String columnName() {
        return columnName;
    }

    @Override
    public ValueConverter<?, ?> valueConvertor() {
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

    public void setValueConverter(ValueConverter<?, ?> valueConvertor) {
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

    /// 获取此属性的表达式路径节点。
    ///
    /// 使用双重检查锁定进行延迟初始化，以确保线程安全，
    /// 同时避免不必要的同步开销。
    ///
    /// @return 路径节点表达式
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
