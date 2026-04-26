package io.github.nextentity.integration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;

/**
 * 销售订单实体，用于测试投影 @Join 注解。
 *
 * <p>
 * 关键设计：该实体<b>不包含</b> {@code @ManyToOne} 或 {@code @JoinColumn} 注解，
 * 仅通过 {@code customerId} 字段保存外键值。
 * 关联关系完全由投影 DTO 中的 {@link io.github.nextentity.core.annotation.Join} 注解声明。
 */
@Entity
public class SalesOrder {

    @Id
    private Long id;

    private String orderNo;

    private Long customerId;

    private BigDecimal amount;

    public SalesOrder() {
    }

    public SalesOrder(Long id, String orderNo, Long customerId, BigDecimal amount) {
        this.id = id;
        this.orderNo = orderNo;
        this.customerId = customerId;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "SalesOrder{" +
               "id=" + id +
               ", orderNo='" + orderNo + '\'' +
               ", customerId=" + customerId +
               ", amount=" + amount +
               '}';
    }
}
