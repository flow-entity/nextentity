package io.github.nextentity.api.model;

import java.util.List;

/// 元组接口，表示包含多个元素的对象。
///
/// 用于表示查询结果中的一行数据，支持按索引获取元素。
///
/// ## 使用示例
///
/// ```java
/// // 查询返回元组
/// List<Tuple2<String, Integer>> results = repository.query()
///     .select(User::getName, User::getAge)
///     .list();
///
/// // 访问元组元素
/// for (Tuple2<String, Integer> tuple : results) {
///     String name = tuple.get0();  // 第一个元素
///     Integer age = tuple.get1();  // 第二个元素
/// }
///
/// // 使用动态索引
/// Tuple tuple = results.get(0);
/// String name = tuple.get(0);
/// Integer age = tuple.get(1);
///
/// // 转换为列表或数组
/// List<Object> list = tuple.toList();
/// Object[] array = tuple.toArray();
/// ```
///
/// @author HuangChengwei
/// @since 1.0.0
public interface Tuple extends Iterable<Object> {

    /// 按索引获取元素。
    ///
    /// @param index 索引
    /// @param <T>   元素类型
    /// @return 元素
    <T> T get(int index);

    /// 获取元组大小。
    ///
    /// @return 元组大小
    int size();

    /// 转换为列表。
    ///
    /// @return 元素列表
    List<Object> toList();

    /// 转换为数组。
    ///
    /// @return 元素数组
    Object[] toArray();

}