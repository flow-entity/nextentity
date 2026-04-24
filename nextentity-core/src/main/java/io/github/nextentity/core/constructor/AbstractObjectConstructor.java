package io.github.nextentity.core.constructor;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.jdbc.Arguments;

import java.util.Collection;
import java.util.List;

/// TODO 添加注释
///
/// @author HuangChengwei
/// @since 2.2.2
public abstract class AbstractObjectConstructor implements ValueConstructor {

    protected final Class<?> resultType;
    protected final Collection<PropertyBinding> properties;


    public AbstractObjectConstructor(Class<?> resultType,
                                     Collection<PropertyBinding> properties) {
        this.resultType = resultType;
        this.properties = properties;
    }

    /// 获取结果类型
    public Class<?> getResultType() {
        return resultType;
    }

    @Override
    public List<Column> columns() {
        return properties.stream()
                .flatMap(PropertyBinding::getColumns)
                .toList();
    }

    @Override
    public Object construct(Arguments arguments) {
        try {
            return constructConcrete(arguments);
        } catch (ReflectiveOperationException e) {
            throw new ReflectiveException(e);
        }
    }

    protected abstract Object constructConcrete(Arguments arguments) throws ReflectiveOperationException;


}
