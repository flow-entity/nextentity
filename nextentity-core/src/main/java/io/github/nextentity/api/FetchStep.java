package io.github.nextentity.api;

import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.core.util.Paths;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/// Fetch step interface, providing methods for fetching associated data.
///
/// Extends WhereStep, used to specify associated data that needs to be preloaded.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 1.0.0
public interface FetchStep<T> extends BaseWhereStep<T, T> {

    /// Fetch associated data corresponding to the specified list of path expressions.
    ///
    /// @param expressions List of path expressions
    /// @return WhereStep instance
    BaseWhereStep<T, T> fetch(List<PathExpression<T, ?>> expressions);

    /// Fetch associated data corresponding to the specified path expression.
    ///
    /// @param path Path expression
    /// @return WhereStep instance
    default BaseWhereStep<T, T> fetch(PathExpression<T, ?> path) {
        return fetch(List.of(path));
    }

    /// Fetch associated data corresponding to the specified two path expressions.
    ///
    /// @param p0 First path expression
    /// @param p1 Second path expression
    /// @return WhereStep instance
    default BaseWhereStep<T, T> fetch(PathExpression<T, ?> p0, PathExpression<T, ?> p1) {
        return fetch(List.of(p0, p1));
    }

    /// Fetch associated data corresponding to the specified three path expressions.
    ///
    /// @param p0 First path expression
    /// @param p1 Second path expression
    /// @param p3 Third path expression
    /// @return WhereStep instance
    default BaseWhereStep<T, T> fetch(PathExpression<T, ?> p0, PathExpression<T, ?> p1, PathExpression<T, ?> p3) {
        return fetch(List.of(p0, p1, p3));
    }

    /// Fetch associated data corresponding to the specified collection of paths.
    ///
    /// @param paths Collection of paths
    /// @return WhereStep instance
    default BaseWhereStep<T, T> fetch(Collection<Path<T, ?>> paths) {
        EntityRoot<T> root = Paths.root();
        List<PathExpression<T, ?>> result = new ArrayList<>(paths.size());
        for (Path<T, ?> path : paths) {
            EntityPath<T, ?> tEntityPathExpression = root.get(path);
            result.add(tEntityPathExpression);
        }
        List<PathExpression<T, ?>> list = Collections.unmodifiableList(result);
        return fetch(list);
    }

    /// Fetch associated data corresponding to the specified path.
    ///
    /// @param path Path
    /// @return WhereStep instance
    default BaseWhereStep<T, T> fetch(Path<T, ?> path) {
        EntityRoot<T> root = Paths.root();
        return fetch(root.get(path));
    }

    /// Fetch associated data corresponding to the specified two paths.
    ///
    /// @param p0 First path
    /// @param p1 Second path
    /// @return WhereStep instance
    default BaseWhereStep<T, T> fetch(Path<T, ?> p0, Path<T, ?> p1) {
        EntityRoot<T> root = Paths.root();
        return fetch(root.get(p0), root.get(p1));
    }

    /// Fetch associated data corresponding to the specified three paths.
    ///
    /// @param p0 First path
    /// @param p1 Second path
    /// @param p3 Third path
    /// @return WhereStep instance
    default BaseWhereStep<T, T> fetch(Path<T, ?> p0, Path<T, ?> p1, Path<T, ?> p3) {
        EntityRoot<T> root = Paths.root();
        return fetch(root.get(p0), root.get(p1), root.get(p3));
    }

}
