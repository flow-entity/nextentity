package io.github.nextentity.core.constructor;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.jdbc.Arguments;

import java.util.Collection;
import java.util.List;

/// 对象构造器基类，提供属性绑定和反射调用的通用逻辑。
///
/// 子类只需实现 {@link #constructConcrete} 方法，
/// 定义具体的对象创建方式（如构造函数调用、代理创建等）。
///
/// @author HuangChengwei
/// @since 2.2.2
public abstract class AbstractObjectConstructor implements ValueConstructor {

    protected final Class<?> resultType;
    protected final Collection<PropertyBinding> properties;


    /// @param resultType 结果类型
    /// @param properties 属性绑定列表
    public AbstractObjectConstructor(Class<?> resultType,
                                     Collection<PropertyBinding> properties) {
        this.resultType = resultType;
        this.properties = properties;
    }

    /// 获取结果类型
    ///
    /// @return 结果类型的 Class 对象
    public Class<?> getResultType() {
        return resultType;
    }

    @Override
    public List<SelectItem> columns() {
        return properties.stream()
                .flatMap(PropertyBinding::getColumns)
                .toList();
    }

    /// 构造值，将 ReflectiveOperationException 转为 ReflectiveException
    @Override
    public Object construct(Arguments arguments) {
        try {
            return constructConcrete(arguments);
        } catch (ReflectiveOperationException e) {
            throw new ReflectiveException(e);
        }
    }

    /// 具体的对象构造逻辑，由子类实现
    protected abstract Object constructConcrete(Arguments arguments) throws ReflectiveOperationException;


}
