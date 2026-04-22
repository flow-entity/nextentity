package io.github.nextentity.core.constructor;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.reflect.schema.impl.DefaultSchema;
import io.github.nextentity.jdbc.Arguments;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/// 对象构造器
///
/// 实现 ValueConstructor，持有 PropertyBinding[]。
/// 每个绑定关联一个属性和一个值构造器。
/// 支持嵌套构造（值构造器可以是 ObjectConstructor）。
///
/// @author HuangChengwei
/// @since 2.2.2
public class ObjectConstructor implements ValueConstructor {

    private final Class<?> resultType;
    private final PropertyBinding[] properties;

    /// 缓存的 Constructor（效率优化）
    private final Constructor<?> constructor;

    public ObjectConstructor(Class<?> resultType,
                             PropertyBinding[] properties) {
        this.resultType = resultType;
        this.properties = properties;
        Constructor<?> constructor = DefaultSchema.of(resultType).getConstructor();
        this.constructor = Objects.requireNonNull(constructor);
    }

    /// 获取结果类型
    public Class<?> getResultType() {
        return resultType;
    }

    @Override
    public List<Column> columns() {
        return Arrays.stream(properties)
                .flatMap(PropertyBinding::getColumns)
                .toList();
    }

    @Override
    public Object construct(Arguments arguments) {
        try {
            Object instance = constructor.newInstance();
            for (PropertyBinding prop : properties) {
                Object value = prop.valueConstructor().construct(arguments);
                if (value != null) {
                    prop.attribute().set(instance, value);
                }
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException
                 | InvocationTargetException e) {
            throw new ReflectiveException(e);
        }
    }
}