package io.github.nextentity.core.constructor;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.reflect.schema.impl.DefaultAccessor;
import io.github.nextentity.jdbc.Arguments;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Objects;

/// 对象构造器
///
/// 用于具体类（非接口、非 Record）的对象构造。
/// 通过无参构造函数创建实例，再通过 setter 设置属性值。
/// 当所有属性值均为 null 时，返回 null 而非构造空实例。
///
/// 接口类型请使用 {@link ProxyConstructor}，
/// Record 类型请使用 {@link RecordConstructor}。
///
/// @author HuangChengwei
/// @since 2.2.2
public class ObjectConstructor extends AbstractObjectConstructor {

    /// 缓存的无参构造函数
    private final Constructor<?> constructor;

    /// 是否为根级投影/实体构造。
    /// 根级构造即使所有属性值为 null 也必须返回非 null 对象（空实例），
    /// 而非根级的嵌入式属性在所有字段为 null 时返回 null，避免创建无意义的嵌套空对象。
    private final boolean root;

    public ObjectConstructor(Class<?> resultType, Collection<PropertyBinding> properties) {
        this(resultType, properties, false);
    }

    public ObjectConstructor(Class<?> resultType, Collection<PropertyBinding> properties, boolean root) {
        if (resultType.isInterface()) {
            throw new ReflectiveException("Cannot create ObjectConstructor for interface types");
        }
        super(resultType, properties);
        Constructor<?> constructor = ReflectUtil.getDefaultConstructor(resultType);
        this.constructor = Objects.requireNonNull(constructor);
        this.root = root;
    }

    @Override
    public Object constructConcrete(Arguments arguments) throws ReflectiveOperationException {
        Object instance = root ? constructor.newInstance() : null;
        for (PropertyBinding prop : properties) {
            Object value = prop.valueConstructor().construct(arguments);
            if (value != null) {
                if (instance == null) {
                    instance = constructor.newInstance();
                }
                prop.attribute().set(instance, value);
            }
        }
        return instance;
    }
}
