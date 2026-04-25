package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.dto.OrderWithCustomer;
import io.github.nextentity.integration.entity.SalesOrder;
import io.github.nextentity.integration.fast.FastIntegrationTestProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 投影 {@link io.github.nextentity.core.annotation.Join} 注解集成测试。
 *
 * <p>
 * 验证场景：源实体 {@code SalesOrder} 不包含 @ManyToOne 等 JPA 关联注解，
 * 仅在投影 DTO {@link OrderWithCustomer} 中通过 {@code @Join} 注解声明关联关系，
 * 框架应能正确生成 JOIN SQL 并填充关联对象。
 *
 * @author HuangChengwei
 */
@DisplayName("Projection @Join Annotation Tests")
public class ProjectionJoinAnnotationTest {

    /**
     * 测试通过 @Join 注解投影查询关联实体。
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query order with customer via @Join projection")
    void shouldQueryOrderWithCustomerViaJoinProjection(IntegrationTestContext context) {
        // When - 使用 @Join 投影查询订单及其关联客户
        List<OrderWithCustomer> orders = context.querySalesOrders()
                .select(OrderWithCustomer.class)
                .orderBy(SalesOrder::getId)
                .asc()
                .list();

        // Then - 验证结果包含所有订单且关联客户正确填充
        assertThat(orders).hasSize(4);

        // 验证第一个订单及其关联客户
        OrderWithCustomer firstOrder = orders.get(0);
        assertThat(firstOrder.getId()).isEqualTo(1L);
        assertThat(firstOrder.getOrderNo()).isEqualTo("ORD-001");
        assertThat(firstOrder.getCustomer()).isNotNull();
        assertThat(firstOrder.getCustomer().getId()).isEqualTo(1L);
        assertThat(firstOrder.getCustomer().getName()).isEqualTo("John Doe");
        assertThat(firstOrder.getCustomer().getEmail()).isEqualTo("john@example.com");

        // 验证第二个订单属于同一客户
        OrderWithCustomer secondOrder = orders.get(1);
        assertThat(secondOrder.getCustomer()).isNotNull();
        assertThat(secondOrder.getCustomer().getId()).isEqualTo(1L);

        // 验证第三个订单属于客户2
        OrderWithCustomer thirdOrder = orders.get(2);
        assertThat(thirdOrder.getCustomer()).isNotNull();
        assertThat(thirdOrder.getCustomer().getId()).isEqualTo(2L);
        assertThat(thirdOrder.getCustomer().getName()).isEqualTo("Jane Smith");
    }

    /**
     * 测试 @Join 投影结合 WHERE 条件过滤。
     */
    @ParameterizedTest
    @ArgumentsSource(FastIntegrationTestProvider.class)// TODO use IntegrationTestProvider.class
    @DisplayName("Should filter orders by customer ID via @Join projection")
    void shouldFilterOrdersByCustomerIdViaJoinProjection(IntegrationTestContext context) {
        // When - 查询特定客户的订单
        List<OrderWithCustomer> customer1Orders = context.querySalesOrders()
                .select(OrderWithCustomer.class)
                .where(SalesOrder::getCustomerId)
                .eq(1L)
                .orderBy(SalesOrder::getId)
                .asc()
                .list();

        // Then
        assertThat(customer1Orders).hasSize(2);
        assertThat(customer1Orders.get(0).getOrderNo()).isEqualTo("ORD-001");
        assertThat(customer1Orders.get(1).getOrderNo()).isEqualTo("ORD-002");
        assertThat(customer1Orders.get(0).getCustomer()).isNotNull();
        assertThat(customer1Orders.get(0).getCustomer().getName()).isEqualTo("John Doe");
    }
}
