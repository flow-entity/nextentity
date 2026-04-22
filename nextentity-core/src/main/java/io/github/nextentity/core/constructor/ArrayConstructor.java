package io.github.nextentity.core.constructor;

import io.github.nextentity.core.Tuples;
import io.github.nextentity.jdbc.Arguments;

import java.util.List;

/// 数组构造器
///
/// 数组元素可以是单值或对象。
/// 构造结果为 Tuples.of(array)，用于 Tuple 投影。
///
/// @author HuangChengwei
/// @since 2.2.2
public class ArrayConstructor implements ValueConstructor {

    private final List<ValueConstructor> elements;

    public ArrayConstructor(List<ValueConstructor> elements) {
        this.elements = elements;
    }

    @Override
    public List<Column> columns() {
        return elements.stream()
                .map(ValueConstructor::columns)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public Object construct(Arguments arguments) {
        Object[] array = new Object[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            array[i] = elements.get(i).construct(arguments);
        }
        return Tuples.of(array);
    }
}