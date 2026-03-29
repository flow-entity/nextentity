package io.github.nextentity.api;

import java.util.Collection;
import java.util.List;

/// Query group by step interface, providing methods for adding grouping conditions.
///
/// @param <T> Entity type
/// @param <U> Query result type
/// @author HuangChengwei
/// @since 1.0.0
public interface GroupByStep<T, U> extends OrderByStep<T, U> {

    /// Add a single expression as a grouping condition.
    ///
    /// @param expressions Expression
    /// @return Query having step after grouping
    HavingStep<T, U> groupBy(Expression<T, ?> expressions);

    /// Add multiple expressions as grouping conditions.
    ///
    /// @param expressions List of expressions
    /// @return Query having step after grouping
    HavingStep<T, U> groupBy(List<? extends Expression<T, ?>> expressions);

    /// Add a single path as a grouping condition.
    ///
    /// @param path Path
    /// @return Query having step after grouping
    HavingStep<T, U> groupBy(PathRef<T, ?> path);

    /// Add multiple paths as grouping conditions.
    ///
    /// @param paths Collection of paths
    /// @return Query having step after grouping
    HavingStep<T, U> groupBy(Collection<PathRef<T, ?>> paths);

    /// Add two paths as grouping conditions.
    ///
    /// @param p0 First path
    /// @param p1 Second path
    /// @return Query having step after grouping
    default HavingStep<T, U> groupBy(PathRef<T, ?> p0, PathRef<T, ?> p1) {
        return groupBy(List.of(p0, p1));
    }

    /// Add three paths as grouping conditions.
    ///
    /// @param p0 First path
    /// @param p1 Second path
    /// @param p2 Third path
    /// @return Query having step after grouping
    default HavingStep<T, U> groupBy(PathRef<T, ?> p0, PathRef<T, ?> p1, PathRef<T, ?> p2) {
        return groupBy(List.of(p0, p1, p2));
    }

    /// Add four paths as grouping conditions.
    ///
    /// @param p0 First path
    /// @param p1 Second path
    /// @param p2 Third path
    /// @param p3 Fourth path
    /// @return Query having step after grouping
    default HavingStep<T, U> groupBy(PathRef<T, ?> p0, PathRef<T, ?> p1, PathRef<T, ?> p2, PathRef<T, ?> p3) {
        return groupBy(List.of(p0, p1, p2, p3));
    }

    /// Add five paths as grouping conditions.
    ///
    /// @param p0 First path
    /// @param p1 Second path
    /// @param p2 Third path
    /// @param p3 Fourth path
    /// @param p4 Fifth path
    /// @return Query having step after grouping
    default HavingStep<T, U> groupBy(PathRef<T, ?> p0, PathRef<T, ?> p1, PathRef<T, ?> p2, PathRef<T, ?> p3, PathRef<T, ?> p4) {
        return groupBy(List.of(p0, p1, p2, p3, p4));
    }

    /// Add six paths as grouping conditions.
    ///
    /// @param p0 First path
    /// @param p1 Second path
    /// @param p2 Third path
    /// @param p3 Fourth path
    /// @param p4 Fifth path
    /// @param p5 Sixth path
    /// @return Query having step after grouping
    default HavingStep<T, U> groupBy(PathRef<T, ?> p0, PathRef<T, ?> p1, PathRef<T, ?> p2, PathRef<T, ?> p3, PathRef<T, ?> p4, PathRef<T, ?> p5) {
        return groupBy(List.of(p0, p1, p2, p3, p4, p5));
    }
}
