package io.github.nextentity.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/// PostgreSQL SQL 方言实现
///
/// PostgreSQL 特性：
/// - 使用双引号 (") 作为标识符引用字符
/// - 分页语法：LIMIT limit OFFSET offset
/// - 日期类型占位符使用 ::timestamp 类型转换
/// - UPDATE JOIN 语法：UPDATE table AS alias SET ... FROM other WHERE join_conditions AND ...
/// - DELETE JOIN 语法：DELETE FROM table AS alias USING other WHERE join_conditions AND ...
///
/// @author HuangChengwei
/// @since 2.0
public class PostgresqlDialect implements SqlDialect {

    /// 静态注册方言
    static {
        SqlDialect.register(new PostgresqlDialect());
    }

    @Override
    public int priority() {
        return 2000;
    }

    @Override
    public boolean matches(DatabaseMetaData metaData) throws SQLException {
        return metaData.getDriverName().toLowerCase().contains("postgresql");
    }

    @Override
    public String leftQuotedIdentifier() {
        return "\"";
    }

    @Override
    public String rightQuotedIdentifier() {
        return "\"";
    }

    @Override
    public void appendLimitOffset(StringBuilder sql, List<Object> args, int offset, int limit) {
        // PostgreSQL style: LIMIT limit OFFSET offset
        if (offset > 0 || limit > 0) {
            sql.append(" limit ? offset ?");
            args.add(limit < 0 ? MAX_LIMIT : limit);
            args.add(Math.max(offset, 0));
        }
    }

    @Override
    public String typedPlaceholder(Class<?> type) {
        // PostgreSQL uses ::timestamp cast for date types
        if (java.util.Date.class.isAssignableFrom(type) || java.sql.Timestamp.class.isAssignableFrom(type)) {
            return "?::timestamp";
        }
        return "?";
    }

    // ========== UPDATE/DELETE JOIN 方法实现 ==========

    @Override
    public void appendUpdateClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // PostgreSQL: UPDATE table AS alias SET ...
        sql.append("update ").append(table).append(" as ").append(alias);
    }

    @Override
    public void appendUpdateFromClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // PostgreSQL: FROM other1 alias1, other2 alias2 (如有 JOIN)
        // JOIN 表列表由 Builder 追加，逗号分隔
        if (hasJoin) {
            sql.append(" from ");
            // JOIN 表列表由 Builder 追加
        }
    }

    @Override
    public void appendDeleteClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // PostgreSQL: DELETE FROM table AS alias ...
        sql.append("delete from ").append(table).append(" as ").append(alias);
    }

    @Override
    public void appendDeleteFromClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // PostgreSQL: USING other1 alias1, other2 alias2 (如有 JOIN)
        if (hasJoin) {
            sql.append(" using ");
            // JOIN 表列表由 Builder 追加
        }
    }

    @Override
    public void appendWhereClause(StringBuilder sql, WhereClauseContext context) {
        // PostgreSQL: JOIN 条件放在 WHERE 中
        sql.append(" where ");
        String delimiter = "";
        for (String condition : context.joinConditions()) {
            sql.append(delimiter).append(condition);
            delimiter = " and ";
        }
        if (!context.isNullOrTrue(context.whereCondition())) {
            sql.append(delimiter);
            context.appendPredicate(context.whereCondition());
        }
    }

}