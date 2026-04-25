package io.github.nextentity.integration.dto;

import io.github.nextentity.core.annotation.Join;
import io.github.nextentity.integration.entity.Customer;

import java.math.BigDecimal;

/**
 * 销售订单投影，通过 {@link Join} 注解实现无实体关联的 JOIN 查询。
 *
 * <p>
 * 源实体 {@code SalesOrder} 本身没有声明 @ManyToOne 关联，
 * 此投影通过 {@code @Join} 注解显式指定 JOIN 条件：
 * <ul>
 *   <li>{@code target} = 要 JOIN 的目标实体类型 Customer</li>
 *   <li>{@code sourceAttribute} = 源实体 SalesOrder 中用于 JOIN 的字段名 customerId</li>
 *   <li>{@code targetAttribute} = 目标实体 Customer 中用于 JOIN 的字段名 id</li>
 * </ul>
 *
 * <p>
 * 查询效果等价于 SQL：
 * <pre>{@code
 * SELECT o.id, o.order_no, o.amount, c.id, c.name, c.email
 * FROM sales_order o
 * LEFT JOIN customer c ON o.customer_id = c.id
 * }</pre>
 */
public interface OrderWithCustomer {

    Long getId();

    String getOrderNo();

    BigDecimal getAmount();

    @Join(
            target = Customer.class,
            sourceAttribute = "customerId",
            targetAttribute = "id"
    )
    Customer getCustomer();
}
