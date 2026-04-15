package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.PathRef;
import io.github.nextentity.core.PathReference;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

/// 表达式构建器的抽象基类。
///
/// 提供构建表达式树的通用实现，支持比较运算符、逻辑运算符和空安全条件操作。
///
/// @param <T> 实体类型
/// @param <U> 被比较的值类型
/// @param <B> 方法链式调用的构建器返回类型
/// @author HuangChengwei
/// @since 1.0.0
public abstract class AbstractExpressionBuilder<T, U, B> implements ExpressionTree {

    private final ExpressionNode root;

    /// 使用指定的根节点创建新的表达式构建器。
    ///
    /// @param root 根表达式节点
    public AbstractExpressionBuilder(ExpressionNode root) {
        this.root = root;
    }

    /// 获取根表达式节点。
    ///
    /// @return 根节点
    public ExpressionNode getRoot() {
        return root;
    }

    /// 检查值是否等于指定值。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B eq(U value) {
        ExpressionNode operate = operate(Operator.EQ, getNode(value));
        return next(operate);
    }

    /// 检查值是否等于指定值（如果值不为空）。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B eqIfNotNull(U value) {
        return value == null ? operateNull() : eq(value);
    }

    /// 检查值是否等于表达式。
    ///
    /// @param value 要比较的表达式
    /// @return 表达式构建器实例
    public B eq(Expression<T, U> value) {
        return next(operate(Operator.EQ, getNode(value)));
    }

    /// 检查值是否不等于指定值。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B ne(U value) {
        return next(operate(Operator.NE, getNode(value)));
    }

    /// 检查值是否不等于指定值（如果值不为空）。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B neIfNotNull(U value) {
        return value == null ? operateNull() : ne(value);
    }

    /// 检查值是否不等于表达式。
    ///
    /// @param value 要比较的表达式
    /// @return 表达式构建器实例
    public B ne(Expression<T, U> value) {
        return next(operate(Operator.NE, getNode(value)));
    }

    /// 检查值是否在指定表达式列表中。
    ///
    /// @param expressions 表达式列表
    /// @return 表达式构建器实例
    public B in(@NonNull Expression<T, List<U>> expressions) {
        return next(operate(Operator.IN, getNode(expressions)));
    }

    /// 检查值是否在指定值数组中。
    ///
    /// @param values 值数组
    /// @return 表达式构建器实例
    @SafeVarargs
    public final B in(U... values) {
        ExpressionNode[] nodes = Arrays.stream(values).map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes));
    }

    /// 检查值是否在指定表达式列表中。
    ///
    /// @param values 表达式列表
    /// @return 表达式构建器实例
    public B in(@NonNull List<? extends Expression<T, U>> values) {
        ExpressionNode[] nodes = values.stream().map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes));
    }

    /// 检查值是否在指定集合中。
    ///
    /// @param values 值集合
    /// @return 表达式构建器实例
    public B in(@NonNull Collection<? extends U> values) {
        ExpressionNode[] nodes = values.stream().map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes));
    }

    /// 检查值是否不在指定值数组中。
    ///
    /// @param values 值数组
    /// @return 表达式构建器实例
    @SafeVarargs
    public final B notIn(U... values) {
        ExpressionNode[] nodes = Arrays.stream(values).map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes).operate(Operator.NOT));
    }

    /// 检查值是否不在指定表达式列表中。
    ///
    /// @param values 表达式列表
    /// @return 表达式构建器实例
    public B notIn(@NonNull List<? extends Expression<T, U>> values) {
        ExpressionNode[] nodes = values.stream().map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes).operate(Operator.NOT));
    }

    /// 检查值是否不在指定集合中。
    ///
    /// @param values 值集合
    /// @return 表达式构建器实例
    public B notIn(@NonNull Collection<? extends U> values) {
        ExpressionNode[] nodes = values.stream().map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes).operate(Operator.NOT));
    }

    /// 检查值是否在指定集合中（如果集合不为 null）。
    ///
    /// @param values 值集合，可为 null
    /// @return 表达式构建器实例
    public B inIfNotNull(Collection<? extends U> values) {
        return values == null ? operateNull() : in(values);
    }

    /// 检查值是否不在指定集合中（如果集合不为 null）。
    ///
    /// @param values 值集合，可为 null
    /// @return 表达式构建器实例
    public B notInIfNotNull(Collection<? extends U> values) {
        return values == null ? operateNull() : notIn(values);
    }

    /// 检查值是否为空。
    ///
    /// @return 表达式构建器实例
    public B isNull() {
        return next(operate(Operator.IS_NULL));
    }

    /// 检查值是否不为空。
    ///
    /// @return 表达式构建器实例
    public B isNotNull() {
        return next(operate(Operator.IS_NOT_NULL));
    }

    /// 检查值是否大于等于表达式值。
    ///
    /// @param expression 表达式
    /// @return 表达式构建器实例
    public B ge(Expression<T, U> expression) {
        return next(operate(Operator.GE, getNode(expression)));
    }

    /// 检查值是否大于表达式值。
    ///
    /// @param expression 表达式
    /// @return 表达式构建器实例
    public B gt(Expression<T, U> expression) {
        return next(operate(Operator.GT, getNode(expression)));
    }

    /// 检查值是否小于等于表达式值。
    ///
    /// @param expression 表达式
    /// @return 表达式构建器实例
    public B le(Expression<T, U> expression) {
        return next(operate(Operator.LE, getNode(expression)));
    }

    /// 检查值是否小于表达式值。
    ///
    /// @param expression 表达式
    /// @return 表达式构建器实例
    public B lt(Expression<T, U> expression) {
        return next(operate(Operator.LT, getNode(expression)));
    }

    /// 检查值是否在两个表达式值之间（包含边界）。
    ///
    /// @param l 左边界表达式
    /// @param r 右边界表达式
    /// @return 表达式构建器实例
    public B between(Expression<T, U> l, Expression<T, U> r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)));
    }

    /// 检查值是否不在两个表达式值之间（包含边界）。
    ///
    /// @param l 左边界表达式
    /// @param r 右边界表达式
    /// @return 表达式构建器实例
    public B notBetween(Expression<T, U> l, Expression<T, U> r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)).operate(Operator.NOT));
    }

    /// 检查值是否大于指定值。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B gt(U value) {
        return next(operate(Operator.GT, getNode(value)));
    }

    /// 检查值是否大于等于指定值。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B ge(U value) {
        return next(operate(Operator.GE, getNode(value)));
    }

    /// 检查值是否小于等于指定值。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B le(U value) {
        return next(operate(Operator.LE, getNode(value)));
    }

    /// 检查值是否小于指定值。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B lt(U value) {
        return next(operate(Operator.LT, getNode(value)));
    }

    /// 检查值是否大于等于指定值（如果值不为空）。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B geIfNotNull(U value) {
        return value == null ? operateNull() : ge(value);
    }

    /// 检查值是否大于指定值（如果值不为空）。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B gtIfNotNull(U value) {
        return value == null ? operateNull() : gt(value);
    }

    /// 检查值是否小于等于指定值（如果值不为空）。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B leIfNotNull(U value) {
        return value == null ? operateNull() : le(value);
    }

    /// 检查值是否小于指定值（如果值不为空）。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B ltIfNotNull(U value) {
        return value == null ? operateNull() : lt(value);
    }

    /// 检查值是否在两个值之间（包含边界）。
    ///
    /// @param l 左边界值
    /// @param r 右边界值
    /// @return 表达式构建器实例
    public B between(U l, U r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)));
    }

    /// 检查值是否不在两个值之间（包含边界）。
    ///
    /// @param l 左边界值
    /// @param r 右边界值
    /// @return 表达式构建器实例
    public B notBetween(U l, U r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)).operate(Operator.NOT));
    }

    /// 检查表达式值是否在两个值之间（包含边界）。
    ///
    /// @param l 左边界表达式
    /// @param r 右边界值
    /// @return 表达式构建器实例
    public B between(Expression<T, U> l, U r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)));
    }

    /// 检查表达式值是否在两个值之间（包含边界）。
    ///
    /// @param l 左边界值
    /// @param r 右边界表达式
    /// @return 表达式构建器实例
    public B between(U l, Expression<T, U> r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)));
    }

    /// 检查表达式值是否不在两个值之间（包含边界）。
    ///
    /// @param l 左边界表达式
    /// @param r 右边界值
    /// @return 表达式构建器实例
    public B notBetween(Expression<T, U> l, U r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)).operate(Operator.NOT));
    }

    /// 检查表达式值是否不在两个值之间（包含边界）。
    ///
    /// @param l 左边界值
    /// @param r 右边界表达式
    /// @return 表达式构建器实例
    public B notBetween(U l, Expression<T, U> r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)).operate(Operator.NOT));
    }

    /// 检查字符串值是否匹配模式（LIKE操作）。
    ///
    /// @param value 模式字符串
    /// @return 表达式构建器实例
    public B like(String value) {
        return next(operate(Operator.LIKE, getNode(value)));
    }

    /// 检查字符串值是否不匹配模式（NOT LIKE操作）。
    ///
    /// @param value 模式字符串
    /// @return 表达式构建器实例
    public B notLike(String value) {
        return next(operate(Operator.LIKE, getNode(value)).operate(Operator.NOT));
    }

    /// 检查字符串值是否匹配模式（如果值不为空）。
    ///
    /// @param value 模式字符串
    /// @return 表达式构建器实例
    public B likeIfNotNull(String value) {
        return value == null ? operateNull() : like(value);
    }

    /// 检查字符串值是否不匹配模式（如果值不为空）。
    ///
    /// @param value 模式字符串
    /// @return 表达式构建器实例
    public B notLikeIfNotNull(String value) {
        return value == null ? operateNull() : notLike(value);
    }

    /// 检查字符串值是否匹配模式（如果值不为空且非空字符串）。
    ///
    /// @param value 模式字符串
    /// @return 表达式构建器实例
    public B likeIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? operateNull() : like(value);
    }

    /// 检查字符串值是否不匹配模式（如果值不为空且非空字符串）。
    ///
    /// @param value 模式字符串
    /// @return 表达式构建器实例
    public B notLikeIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? operateNull() : notLike(value);
    }

    /// 检查字符串值是否等于指定值（如果值不为空且非空字符串）。
    ///
    /// @param value 要比较的值
    /// @return 表达式构建器实例
    public B eqIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? operateNull() : next(operate(Operator.EQ, getNode(value)));
    }

    /// 对表达式取反。
    ///
    /// @return 表达式构建器实例
    public B not() {
        return next(operate(Operator.NOT));
    }

    /// 与指定谓词进行AND操作。
    ///
    /// @param predicate 谓词表达式
    /// @return 表达式构建器实例
    public B and(Expression<T, Boolean> predicate) {
        return next(operate(Operator.AND, getNode(predicate)));
    }

    /// 与指定谓词进行OR操作。
    ///
    /// @param predicate 谓词表达式
    /// @return 表达式构建器实例
    public B or(Expression<T, Boolean> predicate) {
        return next(operate(Operator.OR, getNode(predicate)));
    }

    /// 与指定谓词数组进行AND操作。
    ///
    /// @param predicate 谓词表达式数组
    /// @return 表达式构建器实例
    public B and(Expression<T, Boolean>[] predicate) {
        ExpressionNode[] nodes = Arrays.stream(predicate).map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.AND, nodes));
    }

    /// 与指定谓词数组进行OR操作。
    ///
    /// @param predicate 谓词表达式数组
    /// @return 表达式构建器实例
    public B or(Expression<T, Boolean>[] predicate) {
        ExpressionNode[] nodes = Arrays.stream(predicate).map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.OR, nodes));
    }

    /// 与指定谓词迭代器进行AND操作。
    ///
    /// @param predicates 谓词表达式迭代器
    /// @return 表达式构建器实例
    public B and(Iterable<? extends Expression<T, Boolean>> predicates) {
        ExpressionNode[] nodes = StreamSupport.stream(predicates.spliterator(), false)
                .map(this::getNode).filter(it -> !(it instanceof EmptyNode))
                .toArray(ExpressionNode[]::new);
        return next(operate(Operator.AND, nodes));
    }

    /// 与指定谓词迭代器进行OR操作。
    ///
    /// @param predicates 谓词表达式迭代器
    /// @return 表达式构建器实例
    public B or(Iterable<? extends Expression<T, Boolean>> predicates) {
        ExpressionNode[] nodes = StreamSupport.stream(predicates.spliterator(), false)
                .map(this::getNode)
                .toArray(ExpressionNode[]::new);
        return next(operate(Operator.OR, nodes));
    }

    /// 追加路径引用到当前路径。
    ///
    /// @param path 路径引用
    /// @return 路径节点
    protected PathNode appendPathRef(PathRef<?, ?> path) {
        PathNode target = (PathNode) getRoot();
        String fieldName = PathReference.of(path).getFieldName();
        return target.get(fieldName);
    }

    /// 追加路径到当前路径。
    ///
    /// @param path 路径
    /// @return 路径节点
    protected PathNode appendPath(Expression<?, ?> path) {
        PathNode target = (PathNode) getRoot();
        PathNode next = (PathNode) getNode(path);
        return target.get(next);
    }

    /// 返回空操作。
    ///
    /// @return 表达式构建器实例
    protected B operateNull() {
        return next(EmptyNode.INSTANCE);
    }

    /// 抽象方法，用于创建下一个表达式节点。
    ///
    /// @param operate 操作节点
    /// @return 表达式构建器实例
    abstract protected B next(ExpressionNode operate);

    /// 获取表达式的根节点。
    ///
    /// @param expression 表达式
    /// @return 表达式节点
    protected ExpressionNode getNode(Expression<?, ?> expression) {
        return ((ExpressionTree) expression).getRoot();
    }

    /// 将对象值转换为字面量节点。
    ///
    /// @param value 对象值
    /// @return 字面量节点
    protected ExpressionNode getNode(Object value) {
        return new LiteralNode(value);
    }

    /// 执行指定操作符的操作。
    ///
    /// @param operator 操作符
    /// @return 操作后的表达式节点
    protected ExpressionNode operate(Operator operator) {
        return getRoot().operate(operator);
    }

    /// 执行指定操作符与单个节点的操作。
    ///
    /// @param operator 操作符
    /// @param node 表达式节点
    /// @return 操作后的表达式节点
    protected ExpressionNode operate(Operator operator, ExpressionNode node) {
        return getRoot().operate(operator, node);
    }

    /// 执行指定操作符与两个节点的操作。
    ///
    /// @param operator 操作符
    /// @param node0 第一个表达式节点
    /// @param node1 第二个表达式节点
    /// @return 操作后的表达式节点
    protected ExpressionNode operate(Operator operator, ExpressionNode node0, ExpressionNode node1) {
        return getRoot().operate(operator, node0, node1);
    }

    /// 执行指定操作符与节点数组的操作。
    ///
    /// @param operator 操作符
    /// @param nodes 表达式节点数组
    /// @return 操作后的表达式节点
    protected ExpressionNode operate(Operator operator, ExpressionNode[] nodes) {
        return getRoot().operate(operator, nodes);
    }

    /// 执行指定操作符与节点集合的操作。
    ///
    /// @param operator 操作符
    /// @param nodes 表达式节点集合
    /// @return 操作后的表达式节点
    protected ExpressionNode operate(Operator operator, Collection<ExpressionNode> nodes) {
        return getRoot().operate(operator, nodes);
    }
}