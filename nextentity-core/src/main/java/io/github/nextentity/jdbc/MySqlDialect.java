package io.github.nextentity.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/// MySQL SQL 方言实现
///
/// MySQL 特性：
/// - 使用反引号 (`) 作为标识符引用字符
/// - 分页语法：LIMIT offset,limit 或 LIMIT limit
/// - UPDATE JOIN 语法：UPDATE table alias JOIN other ON ... SET ... WHERE ...
/// - DELETE JOIN 语法：DELETE alias FROM table alias JOIN other ON ... WHERE ...
///
/// @author HuangChengwei
/// @since 2.0
public class MySqlDialect implements SqlDialect {

    /// 静态注册方言
    static {
        SqlDialect.register(new MySqlDialect());
    }

    @Override
    public int priority() {
        return 1000;
    }

    @Override
    public boolean matches(DatabaseMetaData metaData) throws SQLException {
        String driverName = metaData.getDriverName().toLowerCase();
        return driverName.contains("mysql") || driverName.contains("maria");
    }

    @Override
    public String leftQuotedIdentifier() {
        return "`";
    }

    @Override
    public String rightQuotedIdentifier() {
        return "`";
    }

    @Override
    public void appendLimitOffset(StringBuilder sql, List<Object> args, int offset, int limit) {
        if (offset > 0) {
            sql.append(" limit ").append(offset).append(",").append(limit < 0 ? Long.MAX_VALUE : limit);
        } else if (limit >= 0) {
            sql.append(" limit ").append(limit);
        }
    }

    // ========== UPDATE/DELETE JOIN 方法实现 ==========

    @Override
    public boolean supportsUpdateJoinBeforeSetSyntax() {
        return true;  // MySQL 支持 JOIN BEFORE SET 语法
    }

    @Override
    public boolean supportsUpdateFromSyntax() {
        return false;  // MySQL 不支持 UPDATE FROM 语法
    }

    @Override
    public boolean supportsDeleteUsingSyntax() {
        return false;  // MySQL 不支持 DELETE USING 语法
    }

    @Override
    public void appendUpdateClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // MySQL: UPDATE table alias [JOIN ...] SET ...
        sql.append("update ").append(table).append(" ").append(alias);
        // JOIN 子句由 Builder 在 SET 之前追加
    }

    @Override
    public void appendUpdateFromClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // MySQL: FROM 子句为空，JOIN 已在 UPDATE 子句中处理
    }

    @Override
    public void appendDeleteClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // MySQL: DELETE alias FROM table alias ... (有 JOIN)
        //        DELETE FROM table alias ... (无 JOIN)
        if (hasJoin) {
            sql.append("delete ").append(alias);
        } else {
            sql.append("delete from ").append(table).append(" ").append(alias);
        }
    }

    @Override
    public void appendDeleteFromClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // MySQL: FROM table alias JOIN ... (有 JOIN)
        if (hasJoin) {
            sql.append(" from ").append(table).append(" ").append(alias);
            // JOIN 子句由 Builder 追加
        }
    }

    @Override
    public void appendWhereClause(StringBuilder sql, WhereClauseContext context) {
        // MySQL: JOIN 条件已在 ON 子句中，直接追加用户 WHERE 条件
        if (!context.isNullOrTrue(context.whereCondition())) {
            sql.append(" where ");
            context.appendPredicate(context.whereCondition());
        }
    }

}