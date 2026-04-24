package io.github.nextentity.core.constructor;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.reflect.schema.impl.DefaultSchema;
import io.github.nextentity.jdbc.Arguments;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;

/// 对象构造器
///
/// 实现 ValueConstructor，持有 PropertyBinding[]。
/// 每个绑定关联一个属性和一个值构造器。
/// 支持嵌套构造（值构造器可以是 ObjectConstructor）。
///
/// 对于接口类型，使用代理方式构造实例；
/// 对于具体类/Record，使用构造函数 + setter 方式构造。
///
/// @author HuangChengwei
/// @since 2.2.2
public class ObjectConstructor extends AbstractObjectConstructor {

    /// 缓存的 Constructor（接口类型为 null）
    private final Constructor<?> constructor;

    public ObjectConstructor(Class<?> resultType,
                             Collection<PropertyBinding> properties) {
        if (resultType.isInterface()) {
            throw new ReflectiveException("Cannot create ObjectConstructor for interface types");
        }
        super(resultType, properties);
        Constructor<?> constructor = DefaultSchema.of(resultType).getConstructor();
        this.constructor = Objects.requireNonNull(constructor);
    }

    @Override
    public Object construct(Arguments arguments) {
        try {
            Object instance = null;
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
        } catch (InstantiationException | IllegalAccessException
                 | InvocationTargetException e) {
            throw new ReflectiveException(e);
        } catch (RuntimeException e) {
            // TODO wrap Exception
            throw e;
        }
    }
}
