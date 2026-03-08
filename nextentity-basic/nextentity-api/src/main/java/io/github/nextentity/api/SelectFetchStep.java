package io.github.nextentity.api;

import io.github.nextentity.api.model.EntityRoot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 选择获取步骤接口，提供关联数据的获取方法。
 * <p>
 * 继承自SelectWhereStep，用于指定需要预加载的关联数据。
 *
 * @param <T> 实体类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public
interface SelectFetchStep<T> extends SelectWhereStep<T, T> {

    /**
     * 获取指定的路径表达式列表对应的关联数据。
     *
     * @param expressions 路径表达式列表
     * @return SelectWhereStep实例
     */
    SelectWhereStep<T, T> fetch(List<PathExpression<T, ?>> expressions);

    /**
     * 获取指定的路径表达式对应的关联数据。
     *
     * @param path 路径表达式
     * @return SelectWhereStep实例
     */
    default SelectWhereStep<T, T> fetch(PathExpression<T, ?> path) {
        return fetch(List.of(path));
    }

    /**
     * 获取指定的两个路径表达式对应的关联数据。
     *
     * @param p0 第一个路径表达式
     * @param p1 第二个路径表达式
     * @return SelectWhereStep实例
     */
    default SelectWhereStep<T, T> fetch(PathExpression<T, ?> p0, PathExpression<T, ?> p1) {
        return fetch(List.of(p0, p1));
    }

    /**
     * 获取指定的三个路径表达式对应的关联数据。
     *
     * @param p0 第一个路径表达式
     * @param p1 第二个路径表达式
     * @param p3 第三个路径表达式
     * @return SelectWhereStep实例
     */
    default SelectWhereStep<T, T> fetch(PathExpression<T, ?> p0, PathExpression<T, ?> p1, PathExpression<T, ?> p3) {
        return fetch(List.of(p0, p1, p3));
    }

    /**
     * 获取指定的路径集合对应的关联数据。
     *
     * @param paths 路径集合
     * @return SelectWhereStep实例
     */
    default SelectWhereStep<T, T> fetch(Collection<Path<T, ?>> paths) {
        EntityRoot<T> root = root();
        List<PathExpression<T, ?>> result = new ArrayList<>(paths.size());
        for (Path<T, ?> path : paths) {
            EntityPath<T, ?> tEntityPathExpression = root.get(path);
            result.add(tEntityPathExpression);
        }
        List<PathExpression<T, ?>> list = Collections.unmodifiableList(result);
        return fetch(list);
    }

    /**
     * 获取指定的路径对应的关联数据。
     *
     * @param path 路径
     * @return SelectWhereStep实例
     */
    default SelectWhereStep<T, T> fetch(Path<T, ?> path) {
        EntityRoot<T> root = root();
        return fetch(root.get(path));
    }

    /**
     * 获取指定的两个路径对应的关联数据。
     *
     * @param p0 第一个路径
     * @param p1 第二个路径
     * @return SelectWhereStep实例
     */
    default SelectWhereStep<T, T> fetch(Path<T, ?> p0, Path<T, ?> p1) {
        EntityRoot<T> root = root();
        return fetch(root.get(p0), root.get(p1));
    }

    /**
     * 获取指定的三个路径对应的关联数据。
     *
     * @param p0 第一个路径
     * @param p1 第二个路径
     * @param p3 第三个路径
     * @return SelectWhereStep实例
     */
    default SelectWhereStep<T, T> fetch(Path<T, ?> p0, Path<T, ?> p1, Path<T, ?> p3) {
        EntityRoot<T> root = root();
        return fetch(root.get(p0), root.get(p1), root.get(p3));
    }

}
