package io.github.nextentity.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/// 默认方言实现
///
/// 使用标准 SQL 语法，适用于未知数据库类型时的默认选择。
/// 当无法识别具体数据库类型时，此方言提供最通用的 SQL 兼容性。
///
/// 特性：
/// - 使用标准 SQL 双引号作为标识符引用符
/// - 采用 MySQL 风格的分页语法（最广泛支持）
/// - 函数名不做映射（使用标准函数名）
/// - UPDATE/DELETE JOIN 使用 PostgreSQL 风格（FROM/USING）
///
/// 注意：此方言不注册到检测列表，仅作为 fallback 使用。
///
/// @author HuangChengwei
/// @since 2.0
public class DefaultDialect implements SqlDialect {

    @Override
    public boolean matches(DatabaseMetaData metaData) throws SQLException {
        // 默认方言不匹配任何数据库，仅作为 fallback
        return false;
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
        // MySQL style: LIMIT offset,limit (most widely supported)
        if (offset > 0) {
            sql.append(" limit ?,?");
            args.add(offset);
            args.add(limit < 0 ? Long.MAX_VALUE : limit);
        } else if (limit >= 0) {
            sql.append(" limit ?");
            args.add(limit);
        }
    }

    // ========== UPDATE/DELETE JOIN 方法实现 ==========

    @Override
    public void appendUpdateClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // Default: UPDATE table AS alias SET ...
        sql.append("update ").append(table).append(" as ").append(alias);
    }

    @Override
    public void appendUpdateFromClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // Default: FROM other1 alias1, other2 alias2 (如有 JOIN)
        if (hasJoin) {
            sql.append(" from ");
            // JOIN 表列表由 Builder 追加
        }
    }

    @Override
    public void appendDeleteClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // Default: DELETE FROM table AS alias
        sql.append("delete from ").append(table).append(" as ").append(alias);
    }

    @Override
    public void appendDeleteFromClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // Default: USING other1 alias1, other2 alias2 (如有 JOIN)
        if (hasJoin) {
            sql.append(" using ");
            // JOIN 表列表由 Builder 追加
        }
    }

    @Override
    public void appendWhereClause(StringBuilder sql, WhereClauseContext context) {
        // Default: JOIN 条件放在 WHERE 中（PostgreSQL 风格）
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