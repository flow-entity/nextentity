package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.meta.BasicAttribute;
import io.github.nextentity.core.meta.EntitySchema;

import java.util.stream.Stream;

/**
 * @author HuangChengwei
 * @since 2024/4/17 下午1:27
 */
public interface InternalPathExpression extends Expression, Iterable<String> {

    int deep();

    String get(int i);

    InternalPathExpression get(String path);

    InternalPathExpression get(InternalPathExpression path);

    InternalPathExpression parent();

    InternalPathExpression subLength(int len);

    BasicAttribute toAttribute(EntitySchema entityType);

    Stream<String> stream();

}
