package io.github.nextentity.jdbc;

import jakarta.persistence.LockModeType;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/// H2 SQL 方言实现
///
/// H2 数据库特性：
/// - 使用双引号 (") 作为标识符引用字符（标准 SQL 风格）
/// - 分页语法：LIMIT limit OFFSET offset（PostgreSQL 风格）
/// - 只支持 FOR UPDATE 锁模式，不支持 FOR SHARE
/// - 不支持 UPDATE FROM 和 DELETE USING 语法
/// - UPDATE/DELETE JOIN 使用 EXISTS 子查询方式
///
/// H2 是一个轻量级嵌入式数据库，广泛用于测试和开发环境。
/// 它支持多种兼容模式（MySQL、PostgreSQL、Oracle 等），默认使用标准 SQL 语法。
///
/// @author HuangChengwei
/// @since 2.0
public class H2Dialect implements SqlDialect {

    static {
        SqlDialect.register(new H2Dialect());
    }

    @Override
    public int priority() {
        return 4000;
    }

    @Override
    public boolean matches(DatabaseMetaData metaData) throws SQLException {
        return metaData.getDriverName().toLowerCase().contains("h2");
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
        // H2 uses PostgreSQL style: LIMIT limit OFFSET offset
        if (offset > 0 || limit > 0) {
            sql.append(" limit ? offset ?");
            args.add(limit < 0 ? Long.MAX_VALUE : limit);
            args.add(Math.max(offset, 0));
        }
    }

    @Override
    public void appendLockMode(StringBuilder sql, LockModeType lockModeType) {
        // H2 only supports FOR UPDATE, not FOR SHARE
        // For PESSIMISTIC_READ, we use FOR UPDATE as a workaround (still provides row-level locking)
        if (lockModeType == LockModeType.PESSIMISTIC_READ) {
            sql.append(" for update");
        } else if (lockModeType == LockModeType.PESSIMISTIC_WRITE) {
            sql.append(" for update");
        } else if (lockModeType == LockModeType.PESSIMISTIC_FORCE_INCREMENT) {
            sql.append(" for update nowait");
        }
    }

    // ========== UPDATE/DELETE JOIN 方法实现 ==========

    @Override
    public boolean supportsUpdateFromSyntax() {
        return false;  // H2 不支持 UPDATE FROM 语法
    }

    @Override
    public boolean supportsDeleteUsingSyntax() {
        return false;  // H2 不支持 DELETE USING 语法
    }

    @Override
    public boolean supportsSubqueryOnMutatedTable() {
        return true;  // H2 支持在子查询中引用正在更新的表
    }

    @Override
    public void appendUpdateClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // H2: UPDATE table AS alias SET ...
        sql.append("update ").append(table).append(" as ").append(alias);
    }

    @Override
    public void appendUpdateFromClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // H2: 不使用 FROM 子句，JOIN 通过 EXISTS 子查询处理
    }

    @Override
    public void appendDeleteClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // H2: DELETE FROM table AS alias
        sql.append("delete from ").append(table).append(" as ").append(alias);
    }

    @Override
    public void appendDeleteFromClause(StringBuilder sql, String table, String alias, boolean hasJoin) {
        // H2: 不使用 USING 子句，JOIN 通过 EXISTS 子查询处理
    }

    @Override
    public void appendWhereClause(StringBuilder sql, WhereClauseContext context) {
        if (context.joinTables().isEmpty()) {
            // 无 JOIN：直接追加 WHERE 条件
            if (!context.isNullOrTrue(context.whereCondition())) {
                sql.append(" where ");
                context.appendPredicate(context.whereCondition());
            }
        } else {
            // 有 JOIN：使用 EXISTS 子查询
            // WHERE EXISTS (SELECT 1 FROM other1 alias1, other2 alias2
            //               WHERE join_conditions AND user_conditions)
            sql.append(" where exists (select 1 from ");

            // 追加 JOIN 表列表
            String delimiter = "";
            for (JoinTableInfo tableInfo : context.joinTables()) {
                context.appendJoinTable(delimiter, tableInfo);
                delimiter = ", ";
            }

            // 追加 WHERE 条件（包含 JOIN 条件和用户条件）
            sql.append(" where ");
            delimiter = "";
            int index = 0;
            for (String condition : context.joinConditions()) {
                sql.append(delimiter).append(condition);
                delimiter = " and ";
                index++;
            }
            if (!context.isNullOrTrue(context.whereCondition())) {
                sql.append(delimiter);
                context.appendPredicate(context.whereCondition());
            }

            sql.append(")");
        }
    }

}