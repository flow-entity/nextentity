package io.github.nextentity.api;

import io.github.nextentity.api.model.*;

import java.util.Collection;

/// 查询操作接口，提供多种查询方法。
///
/// ## 使用示例
///
/// ```java
/// // 选择特定字段
/// List<Tuple2<String, Integer>> results = repository.query()
///     .select(User::getName, User::getAge)
///     .where(User::getStatus).eq("ACTIVE")
///     .list();
///
/// // 投影到 DTO
/// List<UserDto> dtos = repository.query()
///     .select(UserDto.class)
///     .list();
///
/// // 选择不重复的值
/// List<String> uniqueNames = repository.query()
///     .selectDistinct(User::getName)
///     .list();
/// ```
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 1.0.0
public interface SelectStep<T> extends FetchStep<T> {

    /// 选择指定类型的投影结果。
    ///
    /// @param projectionType 投影类型
    /// @param <R>            投影结果类型
    /// @return 查询条件构建步骤
    <R> BaseWhereStep<T, R> select(Class<R> projectionType);

    /// 选择多个表达式的结果。
    ///
    /// @param paths 表达式列表
    /// @return 多行查询条件构建步骤
    WhereStep<T, Tuple> select(Collection<? extends Expression<T, ?>> paths);

    /// 选择单个表达式的结果。
    ///
    /// @param expression 表达式
    /// @param <R>        表达式结果类型
    /// @return 多行查询条件构建步骤
    <R> WhereStep<T, R> select(Expression<T, R> expression);

    /// 选择单个路径的结果。
    ///
    /// @param path 路径
    /// @param <R>  路径结果类型
    /// @return 多行查询条件构建步骤
    <R> WhereStep<T, R> select(PathRef<T, R> path);

    /// 选择两个路径的结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @return 多行查询条件构建步骤
    <A, B> WhereStep<T, Tuple2<A, B>> select(PathRef<T, A> a, PathRef<T, B> b);

    /// 选择三个路径的结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C> WhereStep<T, Tuple3<A, B, C>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c);

    /// 选择四个路径的结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D> WhereStep<T, Tuple4<A, B, C, D>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d);

    /// 选择五个路径的结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param e   第五个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @param <E> 第五个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E> WhereStep<T, Tuple5<A, B, C, D, E>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e);

    /// 选择六个路径的结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param e   第五个路径
    /// @param f   第六个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @param <E> 第五个路径类型
    /// @param <F> 第六个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F> WhereStep<T, Tuple6<A, B, C, D, E, F>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f);

    /// 选择七个路径的结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param e   第五个路径
    /// @param f   第六个路径
    /// @param g   第七个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @param <E> 第五个路径类型
    /// @param <F> 第六个路径类型
    /// @param <G> 第七个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G> WhereStep<T, Tuple7<A, B, C, D, E, F, G>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g);

    /// 选择八个路径的结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param e   第五个路径
    /// @param f   第六个路径
    /// @param g   第七个路径
    /// @param h   第八个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @param <E> 第五个路径类型
    /// @param <F> 第六个路径类型
    /// @param <G> 第七个路径类型
    /// @param <H> 第八个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G, H> WhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g, PathRef<T, H> h);

    /// 选择九个路径的结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param e   第五个路径
    /// @param f   第六个路径
    /// @param g   第七个路径
    /// @param h   第八个路径
    /// @param i   第九个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @param <E> 第五个路径类型
    /// @param <F> 第六个路径类型
    /// @param <G> 第七个路径类型
    /// @param <H> 第八个路径类型
    /// @param <I> 第九个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G, H, I> WhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g, PathRef<T, H> h, PathRef<T, I> i);

    /// 选择十个路径的结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param e   第五个路径
    /// @param f   第六个路径
    /// @param g   第七个路径
    /// @param h   第八个路径
    /// @param i   第九个路径
    /// @param j   第十个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @param <E> 第五个路径类型
    /// @param <F> 第六个路径类型
    /// @param <G> 第七个路径类型
    /// @param <H> 第八个路径类型
    /// @param <I> 第九个路径类型
    /// @param <J> 第十个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G, H, I, J> WhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g, PathRef<T, H> h, PathRef<T, I> i, PathRef<T, J> j);

    /// 选择两个表达式的结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B> WhereStep<T, Tuple2<A, B>> select(Expression<T, A> a, Expression<T, B> b);

    /// 选择三个表达式的结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C> WhereStep<T, Tuple3<A, B, C>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c);

    /// 选择四个表达式的结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D> WhereStep<T, Tuple4<A, B, C, D>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d);

    /// 选择五个表达式的结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param e   第五个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @param <E> 第五个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E> WhereStep<T, Tuple5<A, B, C, D, E>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e);

    /// 选择六个表达式的结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param e   第五个表达式
    /// @param f   第六个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @param <E> 第五个表达式类型
    /// @param <F> 第六个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F> WhereStep<T, Tuple6<A, B, C, D, E, F>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f);

    /// 选择七个表达式的结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param e   第五个表达式
    /// @param f   第六个表达式
    /// @param g   第七个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @param <E> 第五个表达式类型
    /// @param <F> 第六个表达式类型
    /// @param <G> 第七个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G> WhereStep<T, Tuple7<A, B, C, D, E, F, G>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g);

    /// 选择八个表达式的结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param e   第五个表达式
    /// @param f   第六个表达式
    /// @param g   第七个表达式
    /// @param h   第八个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @param <E> 第五个表达式类型
    /// @param <F> 第六个表达式类型
    /// @param <G> 第七个表达式类型
    /// @param <H> 第八个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G, H> WhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g, Expression<T, H> h);

    /// 选择九个表达式的结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param e   第五个表达式
    /// @param f   第六个表达式
    /// @param g   第七个表达式
    /// @param h   第八个表达式
    /// @param i   第九个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @param <E> 第五个表达式类型
    /// @param <F> 第六个表达式类型
    /// @param <G> 第七个表达式类型
    /// @param <H> 第八个表达式类型
    /// @param <I> 第九个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G, H, I> WhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g, Expression<T, H> h, Expression<T, I> i);

    /// 选择十个表达式的结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param e   第五个表达式
    /// @param f   第六个表达式
    /// @param g   第七个表达式
    /// @param h   第八个表达式
    /// @param i   第九个表达式
    /// @param j   第十个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @param <E> 第五个表达式类型
    /// @param <F> 第六个表达式类型
    /// @param <G> 第七个表达式类型
    /// @param <H> 第八个表达式类型
    /// @param <I> 第九个表达式类型
    /// @param <J> 第十个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G, H, I, J> WhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g, Expression<T, H> h, Expression<T, I> i, Expression<T, J> j);

    /// 选择不重复的指定类型投影结果。
    ///
    /// @param projectionType 投影类型
    /// @param <R>            投影结果类型
    /// @return 查询条件构建步骤
    <R> BaseWhereStep<T, R> selectDistinct(Class<R> projectionType);

    /// 选择不重复的多个表达式结果。
    ///
    /// @param paths 表达式列表
    /// @return 多行查询条件构建步骤
    WhereStep<T, Tuple> selectDistinct(Collection<? extends Expression<T, ?>> paths);

    /// 选择不重复的单个表达式结果。
    ///
    /// @param expression 表达式
    /// @param <R>        表达式结果类型
    /// @return 多行查询条件构建步骤
    <R> WhereStep<T, R> selectDistinct(Expression<T, R> expression);

    /// 选择不重复的单个路径结果。
    ///
    /// @param path 路径
    /// @param <R>  路径结果类型
    /// @return 多行查询条件构建步骤
    <R> WhereStep<T, R> selectDistinct(PathRef<T, R> path);

    /// 选择不重复的两个路径结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @return 多行查询条件构建步骤
    <A, B> WhereStep<T, Tuple2<A, B>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b);

    /// 选择不重复的三个路径结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C> WhereStep<T, Tuple3<A, B, C>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c);

    /// 选择不重复的四个路径结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D> WhereStep<T, Tuple4<A, B, C, D>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d);

    /// 选择不重复的五个路径结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param e   第五个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @param <E> 第五个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E> WhereStep<T, Tuple5<A, B, C, D, E>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e);

    /// 选择不重复的六个路径结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param e   第五个路径
    /// @param f   第六个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @param <E> 第五个路径类型
    /// @param <F> 第六个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F> WhereStep<T, Tuple6<A, B, C, D, E, F>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f);

    /// 选择不重复的七个路径结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param e   第五个路径
    /// @param f   第六个路径
    /// @param g   第七个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @param <E> 第五个路径类型
    /// @param <F> 第六个路径类型
    /// @param <G> 第七个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G> WhereStep<T, Tuple7<A, B, C, D, E, F, G>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g);

    /// 选择不重复的八个路径结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param e   第五个路径
    /// @param f   第六个路径
    /// @param g   第七个路径
    /// @param h   第八个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @param <E> 第五个路径类型
    /// @param <F> 第六个路径类型
    /// @param <G> 第七个路径类型
    /// @param <H> 第八个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G, H> WhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g, PathRef<T, H> h);

    /// 选择不重复的九个路径结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param e   第五个路径
    /// @param f   第六个路径
    /// @param g   第七个路径
    /// @param h   第八个路径
    /// @param i   第九个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @param <E> 第五个路径类型
    /// @param <F> 第六个路径类型
    /// @param <G> 第七个路径类型
    /// @param <H> 第八个路径类型
    /// @param <I> 第九个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G, H, I> WhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g, PathRef<T, H> h, PathRef<T, I> i);

    /// 选择不重复的十个路径结果。
    ///
    /// @param a   第一个路径
    /// @param b   第二个路径
    /// @param c   第三个路径
    /// @param d   第四个路径
    /// @param e   第五个路径
    /// @param f   第六个路径
    /// @param g   第七个路径
    /// @param h   第八个路径
    /// @param i   第九个路径
    /// @param j   第十个路径
    /// @param <A> 第一个路径类型
    /// @param <B> 第二个路径类型
    /// @param <C> 第三个路径类型
    /// @param <D> 第四个路径类型
    /// @param <E> 第五个路径类型
    /// @param <F> 第六个路径类型
    /// @param <G> 第七个路径类型
    /// @param <H> 第八个路径类型
    /// @param <I> 第九个路径类型
    /// @param <J> 第十个路径类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G, H, I, J> WhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g, PathRef<T, H> h, PathRef<T, I> i, PathRef<T, J> j);

    /// 选择不重复的两个表达式结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B> WhereStep<T, Tuple2<A, B>> selectDistinct(Expression<T, A> a, Expression<T, B> b);

    /// 选择不重复的三个表达式结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C> WhereStep<T, Tuple3<A, B, C>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c);

    /// 选择不重复的四个表达式结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D> WhereStep<T, Tuple4<A, B, C, D>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d);

    /// 选择不重复的五个表达式结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param e   第五个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @param <E> 第五个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E> WhereStep<T, Tuple5<A, B, C, D, E>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e);

    /// 选择不重复的六个表达式结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param e   第五个表达式
    /// @param f   第六个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @param <E> 第五个表达式类型
    /// @param <F> 第六个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F> WhereStep<T, Tuple6<A, B, C, D, E, F>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f);

    /// 选择不重复的七个表达式结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param e   第五个表达式
    /// @param f   第六个表达式
    /// @param g   第七个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @param <E> 第五个表达式类型
    /// @param <F> 第六个表达式类型
    /// @param <G> 第七个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G> WhereStep<T, Tuple7<A, B, C, D, E, F, G>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g);

    /// 选择不重复的八个表达式结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param e   第五个表达式
    /// @param f   第六个表达式
    /// @param g   第七个表达式
    /// @param h   第八个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @param <E> 第五个表达式类型
    /// @param <F> 第六个表达式类型
    /// @param <G> 第七个表达式类型
    /// @param <H> 第八个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G, H> WhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g, Expression<T, H> h);

    /// 选择不重复的九个表达式结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param e   第五个表达式
    /// @param f   第六个表达式
    /// @param g   第七个表达式
    /// @param h   第八个表达式
    /// @param i   第九个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @param <E> 第五个表达式类型
    /// @param <F> 第六个表达式类型
    /// @param <G> 第七个表达式类型
    /// @param <H> 第八个表达式类型
    /// @param <I> 第九个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G, H, I> WhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g, Expression<T, H> h, Expression<T, I> i);

    /// 选择不重复的十个表达式结果。
    ///
    /// @param a   第一个表达式
    /// @param b   第二个表达式
    /// @param c   第三个表达式
    /// @param d   第四个表达式
    /// @param e   第五个表达式
    /// @param f   第六个表达式
    /// @param g   第七个表达式
    /// @param h   第八个表达式
    /// @param i   第九个表达式
    /// @param j   第十个表达式
    /// @param <A> 第一个表达式类型
    /// @param <B> 第二个表达式类型
    /// @param <C> 第三个表达式类型
    /// @param <D> 第四个表达式类型
    /// @param <E> 第五个表达式类型
    /// @param <F> 第六个表达式类型
    /// @param <G> 第七个表达式类型
    /// @param <H> 第八个表达式类型
    /// @param <I> 第九个表达式类型
    /// @param <J> 第十个表达式类型
    /// @return 多行查询条件构建步骤
    <A, B, C, D, E, F, G, H, I, J> WhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g, Expression<T, H> h, Expression<T, I> i, Expression<T, J> j);

}