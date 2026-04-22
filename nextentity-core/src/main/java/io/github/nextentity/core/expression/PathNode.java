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
import java.util.function.IntFunction;
import java.util.stream.Stream;

/// 表示实体属性路径的表达式节点。
///
/// 路径是从实体根到特定属性的属性名称序列，
/// 支持关联的嵌套路径。
///
/// 示例：
/// - "name" - 简单属性路径
/// - "address.city" - 通过关联的嵌套路径
///
/// @author HuangChengwei
/// @since 1.0.0
public final class PathNode implements ExpressionNode, ImmutableArray<String> {

    private final String[] path;
    private transient Attribute attribute;

    /// 使用指定的路径段和属性创建 PathNode。
    ///
    /// @param path      路径段
    /// @param attribute 关联的属性元数据（可以为 null）
    public PathNode(String[] path, Attribute attribute) {
        this.path = path;
        this.attribute = attribute;
    }

    /// 使用单个路径段创建 PathNode。
    ///
    /// @param path 单个路径段
    public PathNode(String path) {
        this(new String[]{path});
    }

    /// 使用指定的路径段创建 PathNode。
    ///
    /// @param path 路径段
    public PathNode(String[] path) {
        this(path, null);
    }

    /// 从 Path 方法引用创建 PathNode。
    ///
    /// @param path Path 方法引用
    /// @return 新的 PathNode 实例
    public static PathNode of(PathRef<?, ?> path) {
        if (path instanceof ExpressionTree tree) {
            return (PathNode) tree.getRoot();
        }
        String fieldName = PathReference.of(path).getFieldName();
        return new PathNode(new String[]{fieldName});
    }

    /// 将 Path 引用集合映射为 PathNode 实例。
    ///
    /// @param paths Path 引用
    /// @return PathNode 实例的不可变列表
    public static ImmutableList<ExpressionNode> mapping(Collection<? extends PathRef<?, ?>> paths) {
        return paths.stream().map(PathNode::of).collect(ImmutableList.collector(paths.size()));
    }

    /// 将 Path 引用数组映射为 PathNode 实例。
    ///
    /// @param paths Path 引用
    /// @return PathNode 实例的不可变列表
    public static ImmutableList<ExpressionNode> mapping(PathRef<?, ?>[] paths) {
        return Arrays.stream(paths).map(PathNode::of).collect(ImmutableList.collector(paths.length));
    }

    /// 通过追加路径段创建新的 PathNode。
    ///
    /// @param path 要追加的路径段
    /// @return 扩展路径的新 PathNode
    public PathNode get(String path) {
        String[] newPath = join(path);
        return new PathNode(newPath);
    }

    /// 通过追加 Path 引用创建新的 PathNode。
    ///
    /// @param path 要追加的 Path 引用
    /// @return 扩展路径的新 PathNode
    public PathNode append(PathRef<?, ?> path) {
        String fieldName = PathReference.of(path).getFieldName();
        return get(fieldName);
    }

    /// 创建追加额外路径段的新数组。
    ///
    /// @param path 要追加的路径段
    /// @return 扩展路径的新数组
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
    public <T> T[] toArray(@NonNull IntFunction<T[]> generator) {
        T[] result = generator.apply(path.length);
        // noinspection SuspiciousSystemArraycopy
        System.arraycopy(path, 0, result, 0, result.length);
        return result;
    }

    @Override
    public int size() {
        return path.length;
    }

    @Override
    public @NonNull Iterator<String> iterator() {
        return Arrays.stream(path).iterator();
    }

    /// 通过追加另一个 PathNode 创建新的 PathNode。
    ///
    /// @param other 要追加的 PathNode
    /// @return 组合路径的新 PathNode
    public PathNode get(PathNode other) {
        String[] newPath = new String[size() + other.size()];
        System.arraycopy(this.path, 0, newPath, 0, size());
        System.arraycopy(other.path, 0, newPath, size(), other.size());
        return new PathNode(newPath);
    }

    /// 从模式获取此路径的属性元数据。
    ///
    /// 缓存结果以供后续调用。
    ///
    /// @param schema 要从中解析属性的模式
    /// @return 属性元数据
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

    /// 获取此路径的深度（段数）。
    ///
    /// @return 路径深度
    public int deep() {
        return size();
    }

    /// 创建只包含前 len 个段的新 PathNode。
    ///
    /// @param len 要保留的段数
    /// @return 新的 PathNode，如果 len 不是正数则返回 null
    public PathNode subLength(int len) {
        if (len <= 0) {
            return null;
        }
        String[] strings = new String[len];
        System.arraycopy(path, 0, strings, 0, strings.length);
        return new PathNode(strings);
    }

    @Override
    public String toString() {
        return Arrays.toString(path);
    }
}
