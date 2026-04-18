package io.github.nextentity.jdbc;

import jakarta.persistence.LockModeType;
import java.util.List;

/// H2 SQL 方言实现
///
/// H2 数据库特性：
/// - 使用双引号 (") 作为标识符引用字符（标准 SQL 风格）
/// - 分页语法：LIMIT limit OFFSET offset（PostgreSQL 风格）
/// - 只支持 FOR UPDATE 锁模式，不支持 FOR SHARE
/// - UPDATE JOIN 使用 EXISTS 子查询（不支持 UPDATE FROM 语法）
/// - DELETE JOIN 使用 EXISTS 子查询
/// - JDBC 驱动正确实现批量生成键获取
///
/// H2 是一个轻量级嵌入式数据库，广泛用于测试和开发环境。
/// 它支持多种兼容模式（MySQL、PostgreSQL、Oracle 等），默认使用标准 SQL 语法。
///
/// @author HuangChengwei
/// @since 2.0
public class H2Dialect implements SqlDialect {

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

    @Override
    public UpdateJoinStyle getUpdateJoinStyle() {
        // H2 不支持 UPDATE FROM 和 DELETE USING 语法
        // 使用 EXISTS 子查询处理带 JOIN 的 UPDATE/DELETE
        return UpdateJoinStyle.EXISTS_SUBQUERY;
    }

}