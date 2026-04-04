package io.github.nextentity.core.expression;

import io.github.nextentity.api.SortOrder;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.util.ImmutableList;

import java.util.List;

/// 表示ORDER BY子句中的排序表达式的记录。
///
/// 结合按其排序的表达式和排序顺序（ASC/DESC）。
///
/// @param expression 按其排序的表达式
/// @param order 排序顺序（升序或降序）
/// @author HuangChengwei
/// @since 1.0.0
public record SortExpression(ExpressionNode expression, SortOrder order) {

    /// 将Order对象列表映射到SortExpression实例。
    ///
    /// @param orders 要映射的Order对象
    /// @param <T> 实体类型
    /// @return SortExpression实例的不可变列表
    public static <T> ImmutableList<SortExpression> mapping(List<? extends Order<T>> orders) {
        return orders.stream().map(SortExpression::of).collect(ImmutableList.collector(orders.size()));
    }

    /// 从Order对象创建SortExpression。
    ///
    /// @param order Order对象
    /// @return 新的SortExpression实例
    public static SortExpression of(Order<?> order) {
        ExpressionNode node = ExpressionNodes.getNode(order.expression());
        return new SortExpression(node, order.order());
    }

}
