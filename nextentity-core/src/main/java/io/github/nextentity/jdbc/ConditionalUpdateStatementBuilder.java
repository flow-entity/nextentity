package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

import java.util.Map;

///
/// 条件更新 SQL 语句构建器
///
/// 该类封装了条件更新语句的构建逻辑，继承自 AbstractConditionalStatementBuilder，
/// 使用实例字段保存构建上下文，提供清晰的构建流程和参数管理。
///
/// @author HuangChengwei
/// @since 2.0
///
public class ConditionalUpdateStatementBuilder extends AbstractConditionalStatementBuilder {

    protected final Map<String, Object> setValues;

    public ConditionalUpdateStatementBuilder(EntityType entityType,
                                              Metamodel metamodel,
                                              Map<String, Object> setValues,
                                              ExpressionNode whereCondition,
                                              SqlDialect dialect) {
        super(entityType, metamodel, whereCondition, dialect);
        this.setValues = setValues;
    }

    /// 构建更新语句
    public UpdateSqlStatement build() {
        appendUpdateClause();
        appendSetClause();
        appendWhereCondition();
        return createStatement();
    }

    /// 添加 UPDATE 子句
    protected void appendUpdateClause() {
        sql.append("update ");
        appendTable();
        sql.append(" set ");
    }

    /// 添加 SET 子句
    protected void appendSetClause() {
        String delimiter = "";
        for (Map.Entry<String, Object> entry : setValues.entrySet()) {
            sql.append(delimiter);
            sql.append(leftQuotedIdentifier())
                    .append(entry.getKey())
                    .append(rightQuotedIdentifier());
            sql.append(" = ?");
            params.add(entry.getValue());
            delimiter = ", ";
        }
    }

    /// 创建语句对象
    protected UpdateSqlStatement createStatement() {
        return new UpdateSqlStatement(sql.toString(), params);
    }
}