package io.github.nextentity.core.constructor;

import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntityType;

/// Join 关系描述
///
/// 定义两个表之间的连接关系，支持同一表多次 join。
/// 使用 tableIndex 精确定位表实例。
///
/// @param type               Join 类型
/// @param leftTableIndex     左表索引（指向已存在的表实例）
/// 0 = 主表，>0 = join 表索引
/// @param rightTableIndex    右表索引（此次 join 产生的表索引）
/// 必须 >0，因为 join 产生的表不是主表
/// @param rightEntity        右表 Entity 类型
/// @param leftJoinAttribute  左表连接字段（外键）
/// @param rightJoinAttribute 右表连接字段（主键或关联字段）
/// @param alias              Join 表别名
/// @author HuangChengwei
/// @since 2.2.2
public record JoinInfo(JoinType type, int leftTableIndex, int rightTableIndex, EntityType rightEntity,
                       EntityBasicAttribute leftJoinAttribute, EntityBasicAttribute rightJoinAttribute, String alias) {

    /// 获取 Join 类型
    @Override
    public JoinType type() {
        return type;
    }

    /// 获取左表索引
    @Override
    public int leftTableIndex() {
        return leftTableIndex;
    }

    /// 获取右表索引
    @Override
    public int rightTableIndex() {
        return rightTableIndex;
    }

    /// 获取右表 Entity 类型
    @Override
    public EntityType rightEntity() {
        return rightEntity;
    }

    /// 获取左表连接字段
    @Override
    public EntityBasicAttribute leftJoinAttribute() {
        return leftJoinAttribute;
    }

    /// 获取右表连接字段
    @Override
    public EntityBasicAttribute rightJoinAttribute() {
        return rightJoinAttribute;
    }

    /// 获取 Join 表别名
    @Override
    public String alias() {
        return alias;
    }

    /// 构建 JOIN 子句 SQL
    ///
    /// @param leftAlias 左表别名
    /// @return JOIN 子句 SQL
    public String toJoinClause(String leftAlias) {
        return type.keyword() + " JOIN " +
               rightEntity.tableName() + " " + alias +
               " ON " + leftAlias + "." + leftJoinAttribute.columnName() +
               " = " + alias + "." + rightJoinAttribute.columnName();
    }
}