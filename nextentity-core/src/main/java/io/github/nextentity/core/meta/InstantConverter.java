package io.github.nextentity.core.meta;

import java.sql.Timestamp;
import java.time.Instant;

/// {@link Instant} 与 {@link Timestamp} 之间的转换器。
///
/// 处理 Java 8+ {@link Instant} 与 JDBC {@link Timestamp} 之间的转换，
/// 用于数据库持久化。
///
/// 此转换器是单例，通过 {@link #of()} 获取共享实例。
///
/// @author HuangChengwei
/// @since 1.0.0
public class InstantConverter implements ValueConverter<Instant, Timestamp> {

    private static final InstantConverter INSTANCE = new InstantConverter();

    /// 返回单例实例。
    ///
    /// @return 共享的 Instant 转换器实例
    public static InstantConverter of() {
        return INSTANCE;
    }

    /// 将 Instant 转换为 Timestamp。
    ///
    /// @param attributeValue Instant 值
    /// @return Timestamp 值，如果输入为 null 则返回 null
    @Override
    public Timestamp convertToDatabaseColumn(Instant attributeValue) {
        return attributeValue == null ? null : Timestamp.from(attributeValue);
    }

    /// 将 Timestamp 转换为 Instant。
    ///
    /// @param databaseValue Timestamp 值
    /// @return Instant 值，如果输入为 null 则返回 null
    @Override
    public Instant convertToEntityAttribute(Timestamp databaseValue) {
        return databaseValue == null ? null : databaseValue.toInstant();
    }

    /// 返回 Timestamp 作为数据库列类型。
    ///
    /// @return Timestamp.class
    @Override
    public Class<? extends Timestamp> getDatabaseColumnType() {
        return Timestamp.class;
    }
}
