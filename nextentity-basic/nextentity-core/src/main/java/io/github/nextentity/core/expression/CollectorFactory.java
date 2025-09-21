package io.github.nextentity.core.expression;

import java.util.List;

public interface CollectorFactory {

    <T> List<T> getList(QueryStructure structure);

}
