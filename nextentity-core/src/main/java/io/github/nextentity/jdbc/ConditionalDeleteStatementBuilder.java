package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

///
/// 条件删除 SQL 语句构建器
///
/// 该类封装了条件删除语句的构建逻辑，继承自 AbstractConditionalStatementBuilder，
/// 使用实例字段保存构建上下文，提供清晰的构建流程和参数管理。
///
/// @author HuangChengwei
/// @since 2.0
///
public class ConditionalDeleteStatementBuilder extends AbstractConditionalStatementBuilder {

    public ConditionalDeleteStatementBuilder(EntityType entityType,
                                              Metamodel metamodel,
                                              ExpressionNode whereCondition,
                                              SqlDialect dialect) {
        super(entityType, metamodel, whereCondition, dialect);
    }

    /// 构建删除语句
    public DeleteSqlStatement build() {
        appendDeleteClause();
        appendWhereCondition();
        return createStatement();
    }

    /// 添加 DELETE FROM 子句
    protected void appendDeleteClause() {
        sql.append("delete from ");
        appendTableName();
    }

    /// 创建语句对象
    protected DeleteSqlStatement createStatement() {
        return new DeleteSqlStatement(sql.toString(), params);
    }
}