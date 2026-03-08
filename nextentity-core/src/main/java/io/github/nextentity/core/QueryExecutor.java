package io.github.nextentity.core;

import io.github.nextentity.core.expression.QueryStructure;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface QueryExecutor {
    <T> List<T> getList(@NonNull QueryStructure queryStructure);
}
