package io.github.nextentity.core.expression;

import io.github.nextentity.api.Path;
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

public final class PathNode implements ExpressionNode, ImmutableArray<String> {
    private final String[] path;
    private transient Attribute attribute;

    public PathNode(String[] path, Attribute attribute) {
        this.path = path;
        this.attribute = attribute;
    }

    public PathNode(String path) {
        this(new String[]{path});
    }

    public PathNode(String[] path) {
        this(path, null);
    }

    public static PathNode of(Path<?, ?> path) {
        String fieldName = PathReference.of(path).getFieldName();
        return new PathNode(new String[]{fieldName});
    }

    public static ImmutableList<ExpressionNode> mapping(Collection<? extends Path<?, ?>> paths) {
        return paths.stream().map(PathNode::of).collect(ImmutableList.collector(paths.size()));
    }

    public static ImmutableList<ExpressionNode> mapping(Path<?, ?>[] paths) {
        return Arrays.stream(paths).map(PathNode::of).collect(ImmutableList.collector(paths.length));
    }

    public PathNode get(String path) {
        String[] newPath = join(path);
        return new PathNode(newPath);
    }

    public PathNode append(Path<?, ?> path) {
        String fieldName = PathReference.of(path).getFieldName();
        return get(fieldName);
    }

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

    public PathNode get(PathNode other) {
        String[] newPath = new String[size() + other.size()];
        System.arraycopy(this.path, 0, newPath, 0, size());
        System.arraycopy(other.path, 0, newPath, size(), other.size());
        return new PathNode(newPath);
    }

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

    public int deep() {
        return size();
    }

    public PathNode subLength(int len) {
        if (len <= 0) {
            return null;
        }
        String[] strings = new String[len];
        System.arraycopy(path, 0, strings, 0, strings.length);
        return new PathNode(strings);
    }
}
