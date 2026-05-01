package io.github.nextentity.core.constructor;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.reflect.schema.impl.DefaultAccessor;
import io.github.nextentity.jdbc.Arguments;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;

/// Record 构造器
///
/// 专用于 Java Record 类型的对象构造。
/// 使用 Record 的规范构造函数（canonical constructor）按参数顺序构造实例，
/// 而非使用 setter 方式（Record 没有 setter）。
///
/// 当所有属性值均为 null 时，返回 null 而非构造空 Record 实例。
///
/// @author HuangChengwei
/// @since 2.2.2
public class RecordConstructor extends AbstractObjectConstructor {

    /// 缓存的规范构造函数
    private final Constructor<?> constructor;

    /// 是否为根级投影/实体构造。
    /// 根级构造即使所有属性值为 null 也必须返回非 null Record 实例，
    /// 而非根级的嵌入式属性在所有字段为 null 时返回 null。
    private final boolean root;

    public RecordConstructor(Class<?> resultType,
                             Collection<PropertyBinding> properties) {
        this(resultType, properties, false);
    }

    public RecordConstructor(Class<?> resultType,
                             Collection<PropertyBinding> properties,
                             boolean root) {
        if (!resultType.isRecord()) {
            throw new ReflectiveException(resultType + " is not a record type");
        }
        super(resultType, properties);
        Constructor<?> constructor = DefaultAccessor.getConstructor(resultType);
        this.constructor = Objects.requireNonNull(constructor);
        this.root = root;
    }

    @Override
    public Object constructConcrete(Arguments arguments) throws ReflectiveOperationException{
        return constructRecord(arguments);
    }

    private Object constructRecord(Arguments arguments) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        boolean hasNonnull = false;
        int parameterCount = constructor.getParameterCount();
        Object[] args = new Object[parameterCount];
        for (PropertyBinding prop : properties) {
            Object value = prop.valueConstructor().construct(arguments);
            if (value != null) {
                int ordinal = prop.attribute().accessor().ordinal();
                args[ordinal] = value;
                hasNonnull = true;
            }
        }
        if (hasNonnull || root) {
            return constructor.newInstance(args);
        }
        return null;
    }
}
