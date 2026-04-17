package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.util.Iterators;

///
/// 批量 SQL 语句构建器的抽象基类
///
/// 该类封装了批量操作（INSERT、UPDATE、DELETE）的公共逻辑，
/// 包括参数提取、空值处理、版本字段初始化等。
///
/// @author HuangChengwei
/// @since 2.0
///
public abstract class AbstractBatchStatementBuilder {

    protected final StringBuilder sql = new StringBuilder();
    protected final SqlDialect dialect;

    protected AbstractBatchStatementBuilder(SqlDialect dialect) {
        this.dialect = dialect;
    }

    protected String leftQuotedIdentifier() {
        return dialect.leftQuotedIdentifier();
    }

    protected String rightQuotedIdentifier() {
        return dialect.rightQuotedIdentifier();
    }

    /// 获取参数列表
    ///
    /// 从实体集合中提取指定属性的值作为 SQL 参数。
    /// 对于版本字段，如果值为 null 则初始化为 0；
    /// 对于其他空值，使用 NullParameter 包装以保留类型信息。
    ///
    /// @param entities   实体集合
    /// @param attributes 属性列表
    /// @return 参数列表
    protected Iterable<? extends Iterable<?>> getParameters(Iterable<?> entities,
                                                            Iterable<? extends EntityBasicAttribute> attributes) {
        return Iterators.map(entities, entity -> Iterators.map(attributes, attr -> {
            Object value = attr.getDatabaseValue(entity);
            if (attr.isVersion() && value == null) {
                value = 0;
            }
            return value == null ? new NullParameter(attr.type()) : value;
        }));
    }
}