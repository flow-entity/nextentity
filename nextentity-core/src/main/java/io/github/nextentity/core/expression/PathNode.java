package io.github.nextentity.core.expression;

import io.github.nextentity.api.PathRef;
import io.github.nextentity.core.PathReference;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

///
/// Expression node representing a path to an entity attribute.
/// <p>
/// A path is a sequence of property names that navigate from an entity root
/// to a specific attribute, supporting nested paths for associations.
/// <p>
/// Examples:
/// <ul>
///   <li>"name" - simple attribute path</li>
///   <li>"address.city" - nested path through association</li>
/// </ul>
///
/// @author HuangChengwei
/// @since 1.0.0
///
public final class PathNode implements ExpressionNode, ImmutableArray<String> {

    private final String[] path;
    private transient Attribute attribute;

    ///
    /// Creates a PathNode with the specified path segments and attribute.
    ///
    /// @param path the path segments
    /// @param attribute the associated attribute metadata (can be null)
    ///
    public PathNode(String[] path, Attribute attribute) {
        this.path = path;
        this.attribute = attribute;
    }

    ///
    /// Creates a PathNode with a single path segment.
    ///
    /// @param path the single path segment
    ///
    public PathNode(String path) {
        this(new String[]{path});
    }

    ///
    /// Creates a PathNode with the specified path segments.
    ///
    /// @param path the path segments
    ///
    public PathNode(String[] path) {
        this(path, null);
    }

    ///
    /// Creates a PathNode from a Path method reference.
    ///
    /// @param path the Path method reference
    /// @return a new PathNode instance
    ///
    public static PathNode of(PathRef<?, ?> path) {
        if (path instanceof ExpressionTree tree) {
            return (PathNode) tree.getRoot();
        }
        String fieldName = PathReference.of(path).getFieldName();
        return new PathNode(new String[]{fieldName});
    }

    ///
    /// Maps a collection of Path references to PathNode instances.
    ///
    /// @param paths the Path references
    /// @return an immutable list of PathNode instances
    ///
    public static ImmutableList<ExpressionNode> mapping(Collection<? extends PathRef<?, ?>> paths) {
        return paths.stream().map(PathNode::of).collect(ImmutableList.collector(paths.size()));
    }

    ///
    /// Maps an array of Path references to PathNode instances.
    ///
    /// @param paths the Path references
    /// @return an immutable list of PathNode instances
    ///
    public static ImmutableList<ExpressionNode> mapping(PathRef<?, ?>[] paths) {
        return Arrays.stream(paths).map(PathNode::of).collect(ImmutableList.collector(paths.length));
    }

    ///
    /// Creates a new PathNode by appending a path segment.
    ///
    /// @param path the path segment to append
    /// @return a new PathNode with the extended path
    ///
    public PathNode get(String path) {
        String[] newPath = join(path);
        return new PathNode(newPath);
    }

    ///
    /// Creates a new PathNode by appending a Path reference.
    ///
    /// @param path the Path reference to append
    /// @return a new PathNode with the extended path
    ///
    public PathNode append(PathRef<?, ?> path) {
        String fieldName = PathReference.of(path).getFieldName();
        return get(fieldName);
    }

    ///
    /// Creates a new array with an additional path segment appended.
    ///
    /// @param path the path segment to append
    /// @return a new array with the extended path
    ///
    public String @NonNull [] join(String path) {
        int length = this.path.length;
        String[] newPath = new String[length + 1];
        System.arraycopy(this.path, 0, newPath, 0, length);
        newPath[length] = path;
        return newPath;
    }

    @Override
    public Stream<String> stream() {
        return Arrays.stream(path);
    }

    @Override
    public String get(int index) {
        return path[index];
    }

    @Override
    public int size() {
        return path.length;
    }

    @Override
    public @NonNull Iterator<String> iterator() {
        return Arrays.stream(path).iterator();
    }

    ///
    /// Creates a new PathNode by appending another PathNode.
    ///
    /// @param other the PathNode to append
    /// @return a new PathNode with the combined path
    ///
    public PathNode get(PathNode other) {
        String[] newPath = new String[size() + other.size()];
        System.arraycopy(this.path, 0, newPath, 0, size());
        System.arraycopy(other.path, 0, newPath, size(), other.size());
        return new PathNode(newPath);
    }

    ///
    /// Gets the attribute metadata for this path from a schema.
    /// <p>
    /// Caches the result for subsequent calls.
    ///
    /// @param schema the schema to resolve the attribute from
    /// @return the attribute metadata
    ///
    public Attribute getAttribute(Schema schema) {
        if (attribute == null) {
            attribute = schema.getAttribute(this);
        }
        return attribute;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        PathNode strings = (PathNode) o;
        return Arrays.equals(path, strings.path);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(path);
    }

    ///
    /// Gets the depth (number of segments) of this path.
    ///
    /// @return the path depth
    ///
    public int deep() {
        return size();
    }

    ///
    /// Creates a new PathNode with only the first len segments.
    ///
    /// @param len the number of segments to keep
    /// @return a new PathNode, or null if len is not positive
    ///
    public PathNode subLength(int len) {
        if (len <= 0) {
            return null;
        }
        String[] strings = new String[len];
        System.arraycopy(path, 0, strings, 0, strings.length);
        return new PathNode(strings);
    }
}
