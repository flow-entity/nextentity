package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.AbstractSchemaAttribute;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Attributes;

import java.util.function.Function;

/// {@link JoinAttribute} 的简单实现。
///
/// 此类为关联属性提供了具体实现，
/// 通过提供的函数延迟构建属性。
///
/// 关联属性表示实体关系（多对一、一对一），
/// 包含连接列和引用表的元数据。
///
/// @author HuangChengwei
/// @since 1.0.0
public class SimpleJoinAttribute extends AbstractSchemaAttribute implements JoinAttribute {

    private final Function<JoinAttribute, Attributes> attributesFunction;
    private String joinName;
    private String referencedColumnName;
    private EntityAttribute id;
    private EntityAttribute version;
    private String tableName;

    /// 创建新的 SimpleJoinAttribute 实例。
    ///
    /// @param attributesFunction 构建目标实体属性的函数
    public SimpleJoinAttribute(Function<JoinAttribute, Attributes> attributesFunction) {
        this.attributesFunction = attributesFunction;
    }

    @Override
    public EntityAttribute id() {
        return id;
    }

    @Override
    public EntityAttribute version() {
        return version;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public String joinName() {
        return joinName;
    }

    @Override
    public String referencedColumnName() {
        return referencedColumnName;
    }

    /// 构建目标实体属性并提取 ID 和版本属性。
    ///
    /// @return 目标实体属性
    @Override
    protected Attributes buildAttributes() {
        Attributes attributes = attributesFunction.apply(this);
        EntityAttribute version = null;
        EntityAttribute id = null;
        for (Attribute attribute : attributes) {
            if (attribute instanceof EntityAttribute column) {
                if (column.isId()) {
                    id = column;
                } else if (column.isVersion()) {
                    version = column;
                }
            }
        }
        this.id = id;
        this.version = version;
        return attributes;
    }

    /// 设置连接列名。
    ///
    /// @param joinName 连接列名
    public void setJoinName(String joinName) {
        this.joinName = joinName;
    }

    /// 设置引用（外键）列名。
    ///
    /// @param referencedColumnName 引用列名
    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

    /// 设置目标实体表名。
    ///
    /// @param tableName 表名
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
