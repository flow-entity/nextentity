package io.github.nextentity.integration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

/// Instant 时间字段实体，用于验证时间字面量的时区口径。
///
/// createdAt 显式映射为 TIMESTAMP_UTC，并声明为不带时区的列，
/// 对齐 MySQL 上 Instant 的默认映射与 DATETIME 列。
/// 绑定路径按 UTC 口径下发，内联路径按 TimeZone.getDefault() 渲染。
///
/// 注意：H2 接受 setObject(Instant, TIMESTAMP_WITH_TIMEZONE)，写入时按会话时区转换，
/// 因此在本模块的 H2 环境下两条路径口径一致，这个缺陷只能在 MySQL/PostgreSQL/SQL Server 上暴露。
@Entity
public class InstantEntity {

    @Id
    private Long id;

    private String name;

    @JdbcTypeCode(SqlTypes.TIMESTAMP_UTC)
    @Column(columnDefinition = "timestamp")
    private Instant createdAt;

    public InstantEntity() {
    }

    public InstantEntity(Long id, String name, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "InstantEntity{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}

