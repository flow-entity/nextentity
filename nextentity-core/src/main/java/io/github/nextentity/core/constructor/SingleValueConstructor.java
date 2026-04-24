package io.github.nextentity.core.constructor;

import io.github.nextentity.jdbc.Arguments;

import java.util.List;

/// 单值构造器
///
/// 包含一个列，直接返回该列值。
/// 列来源可以是 PathNode、OperatorNode 或 LiteralNode。
///
/// @author HuangChengwei
/// @since 2.2.2
public record SingleValueConstructor(List<Column> columns) implements ValueConstructor {

    public SingleValueConstructor(Column columns) {
        this(List.of(columns));
    }

    @Override
    public Object construct(Arguments arguments) {
        return arguments.next(columns.getFirst().converter());
    }

}