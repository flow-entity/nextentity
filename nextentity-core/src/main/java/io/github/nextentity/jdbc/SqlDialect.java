package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.ExpressionNodes;
import jakarta.persistence.LockModeType;
import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/// SQL 方言接口
///
/// 定义数据库特定的 SQL 语法差异，包括标识符引用、分页策略、函数名映射、
/// UPDATE/DELETE JOIN 语法等。新增方言只需实现此接口，无需修改 Builder 类。
///
/// @author HuangChengwei
/// @since 2.0.0
public interface SqlDialect {

    /// 返回标识符的左引号字符
    ///
    /// @return 左引号字符（例如 MySQL 使用 "`"，标准 SQL 使用 "\""）
    String leftQuotedIdentifier();

    /// 返回标识符的右引号字符
    ///
    /// @return 右引号字符（例如 MySQL 使用 "`"，标准 SQL 使用 "\""）
    String rightQuotedIdentifier();

    /// 添加 LIMIT 和 OFFSET 子句到 SQL 语句
    ///
    /// 不同数据库有不同的分页语法：
    /// - MySQL: LIMIT offset,limit 或 LIMIT limit
    /// - PostgreSQL: LIMIT limit OFFSET offset
    /// - SQL Server: OFFSET offset ROWS FETCH FIRST limit ROWS ONLY
    ///
    /// @param sql    要追加的 SQL 构建器
    /// @param args   要添加值的参数列表
    /// @param offset 偏移量（无偏移时为 0）
    /// @param limit  限制数（无限制时为 -1）
    void appendLimitOffset(StringBuilder sql, List<Object> args, int offset, int limit);

    /// 将函数名映射到数据库特定的名称
    ///
    /// 有些数据库使用不同的函数名：
    /// - SQL Server 使用 "len" 而不是 "length"
    ///
    /// @param name 标准函数名
    /// @return 数据库特定的函数名
    default String functionName(String name) {
        return name;
    }

    /// 返回数据库是否需要 ORDER BY 子句才能进行分页
    ///
    /// SQL Server 在 OFFSET/FETCH 子句之前需要 ORDER BY。
    ///
    /// @return 如果分页需要 ORDER BY 则返回 true
    default boolean requiresOrderByForPagination() {
        return false;
    }

    /// 返回是否应该将 NOT path 转换为 path = false
    ///
    /// SQL Server 对布尔表达式的 NOT 操作有特殊处理。
    ///
    /// @return 如果应将 NOT path 转换为 path = false 则返回 true
    default boolean shouldConvertNotToEqFalse() {
        return false;
    }

    /// 返回给定属性类型的类型化占位符
    ///
    /// PostgreSQL 对日期类型使用 "::timestamp" 类型转换。
    ///
    /// @param type 属性类型
    /// @return 类型化占位符字符串
    default String typedPlaceholder(Class<?> type) {
        return "?";
    }

    /// 返回数据库是否支持 FOR SHARE/FOR UPDATE 锁模式语法
    ///
    /// SQL Server 不支持此语法，需要使用 WITH (UPDLOCK) 提示。
    ///
    /// @return 如果支持 FOR SHARE/FOR UPDATE 语法则返回 true
    default boolean supportsForUpdateSyntax() {
        return true;
    }

    /// 返回数据库是否支持批量获取生成键
    ///
    /// 某些数据库的 JDBC 驱动在批量插入时可以正确返回所有生成的主键：
    /// - MySQL: 支持（JDBC 驾动正确实现 getGeneratedKeys）
    /// - PostgreSQL: 支持（使用 INSERT ... RETURNING）
    /// - SQL Server: 不完全支持（需要逐条执行）
    ///
    /// @return 如果支持批量获取生成键则返回 true
    default boolean supportsBatchGeneratedKeys() {
        return true;
    }

    /// 返回是否需要为子查询中的聚合列添加别名
    ///
    /// SQL Server 要求子查询中的所有列必须有名称，聚合函数列如 count(id) 需要别名。
    ///
    /// @return 如果需要为聚合列添加别名则返回 true
    default boolean requiresAliasForAggregateColumns() {
        return false;
    }

    /// 添加锁模式子句到 SQL 语句
    ///
    /// 不同数据库有不同的锁模式语法：
    /// - PostgreSQL/MySQL: FOR SHARE, FOR UPDATE, FOR UPDATE NOWAIT
    /// - SQL Server: WITH (ROWLOCK), WITH (UPDLOCK, ROWLOCK), WITH (UPDLOCK, ROWLOCK, NOWAIT)
    ///
    /// @param sql         要追加的 SQL 构建器
    /// @param lockModeType JPA 锁模式类型
    default void appendLockMode(StringBuilder sql, LockModeType lockModeType) {
        if (lockModeType == LockModeType.PESSIMISTIC_READ) {
            sql.append(" for share");
        } else if (lockModeType == LockModeType.PESSIMISTIC_WRITE) {
            sql.append(" for update");
        } else if (lockModeType == LockModeType.PESSIMISTIC_FORCE_INCREMENT) {
            sql.append(" for update nowait");
        }
    }

    // ========== UPDATE/DELETE JOIN 相关方法 ==========

    /// 返回是否支持 UPDATE FROM 语法
    ///
    /// PostgreSQL 风格：UPDATE table alias SET ... FROM other WHERE ...
    /// 不支持的数据库（如 H2、Oracle）需要使用 EXISTS 子查询方式。
    ///
    /// @return 如果支持 UPDATE FROM 语法则返回 true
    default boolean supportsUpdateFromSyntax() {
        return true;
    }

    /// 返回是否支持 DELETE USING 语法
    ///
    /// PostgreSQL 风格：DELETE FROM table USING other WHERE ...
    /// 不支持的数据库（如 H2、Oracle）需要使用 EXISTS 子查询方式。
    ///
    /// @return 如果支持 DELETE USING 语法则返回 true
    default boolean supportsDeleteUsingSyntax() {
        return true;
    }

    /// 返回是否支持 UPDATE JOIN BEFORE SET 语法
    ///
    /// MySQL 风格：UPDATE table alias JOIN other ON ... SET ...
    /// JOIN 在 SET 子句之前。
    ///
    /// @return 如果支持此语法则返回 true
    default boolean supportsUpdateJoinBeforeSetSyntax() {
        return false;
    }

    /// 返回是否支持 UPDATE ALIAS ONLY 语法
    ///
    /// SQL Server 风格：UPDATE alias SET ... FROM table alias JOIN ...
    /// UPDATE 后面只写别名，FROM 子句中写完整表名。
    ///
    /// @return 如果支持此语法则返回 true
    default boolean supportsUpdateAliasOnlySyntax() {
        return false;
    }

    /// 返回是否支持在子查询中引用正在更新的表
    ///
    /// H2、Oracle 等不支持 UPDATE FROM 的数据库返回 true，
    /// 表示需要使用 EXISTS 子查询方式处理带 JOIN 的 UPDATE/DELETE。
    ///
    /// @return 如果支持子查询引用正在更新的表则返回 true
    default boolean supportsSubqueryOnMutatedTable() {
        return false;
    }

    /// 构建 UPDATE 语句的开头部分
    ///
    /// 不同方言的 UPDATE 开头格式：
    /// - MySQL: UPDATE table alias [JOIN...] SET
    /// - PostgreSQL: UPDATE table AS alias SET
    /// - SQL Server: UPDATE alias SET
    /// - H2: UPDATE table AS alias SET
    ///
    /// @param sql     SQL 构建器
    /// @param table   主表名（已引用）
    /// @param alias   主表别名
    /// @param hasJoin 是否有 JOIN 表
    void appendUpdateClause(StringBuilder sql, String table, String alias, boolean hasJoin);

    /// 构建 UPDATE 语句的 FROM/JOIN 子句（在 SET 之后）
    ///
    /// 不同方言的处理：
    /// - MySQL: 空（JOIN 已在 UPDATE 子句中）
    /// - PostgreSQL: FROM other1 alias1, other2 alias2（如有 JOIN）
    /// - SQL Server: FROM table alias JOIN other ON ...（如有 JOIN）
    /// - H2: 空（使用 EXISTS 子查询，不在此处处理）
    ///
    /// @param sql     SQL 构建器
    /// @param table   主表名（已引用）
    /// @param alias   主表别名
    /// @param hasJoin 是否有 JOIN 表
    void appendUpdateFromClause(StringBuilder sql, String table, String alias, boolean hasJoin);

    /// 构建 DELETE 语句的开头部分
    ///
    /// 不同方言的 DELETE 开头格式：
    /// - MySQL: DELETE alias FROM table alias [JOIN...]（无 JOIN 时：DELETE FROM table alias）
    /// - PostgreSQL: DELETE FROM table AS alias（无 JOIN 时）
    ///               DELETE FROM table AS alias USING ...（有 JOIN 时）
    /// - SQL Server: DELETE alias FROM table alias [JOIN...]
    /// - H2: DELETE FROM table AS alias
    ///
    /// @param sql     SQL 构建器
    /// @param table   主表名（已引用）
    /// @param alias   主表别名
    /// @param hasJoin 是否有 JOIN 表
    void appendDeleteClause(StringBuilder sql, String table, String alias, boolean hasJoin);

    /// 构建 DELETE 语句的 FROM/USING/JOIN 子句（在 DELETE 开头之后）
    ///
    /// 不同方言的处理：
    /// - MySQL: FROM table alias JOIN other ON ...（如有 JOIN）
    /// - PostgreSQL: USING other1 alias1, other2 alias2（如有 JOIN）
    /// - SQL Server: FROM table alias JOIN other ON ...（如有 JOIN）
    /// - H2: 空（使用 EXISTS 子查询，不在此处处理）
    ///
    /// @param sql     SQL 构建器
    /// @param table   主表名（已引用）
    /// @param alias   主表别名
    /// @param hasJoin 是否有 JOIN 表
    void appendDeleteFromClause(StringBuilder sql, String table, String alias, boolean hasJoin);

    /// 构建 WHERE 子句（处理 JOIN 条件和用户条件）
    ///
    /// 不同方言的处理：
    /// - MySQL: WHERE user_conditions（JOIN 条件已在 ON 子句中）
    /// - PostgreSQL: WHERE join_conditions AND user_conditions（JOIN 条件在 WHERE 中）
    /// - SQL Server: WHERE user_conditions（JOIN 条件已在 ON 子句中）
    /// - H2: WHERE EXISTS (SELECT 1 FROM other WHERE join_conditions AND user_conditions)
    ///
    /// @param sql     SQL 构建器
    /// @param context WHERE 子句构建上下文，包含 JOIN 表信息和条件追加方法
    void appendWhereClause(StringBuilder sql, WhereClauseContext context);

    /// WHERE 子句构建上下文接口
    ///
    /// 提供方言构建 WHERE 子句所需的所有信息和方法
    interface WhereClauseContext {

        /// 返回主表别名
        ///
        /// @return 主表别名（如 "e_"）
        String mainTableAlias();

        /// 返回 JOIN 表信息列表
        ///
        /// @return JOIN 表信息列表（表名 + 别名）
        List<JoinTableInfo> joinTables();

        /// 返回 JOIN 连接条件列表
        ///
        /// @return JOIN 连接条件列表（如 "e_.department_id = d_.id"）
        List<String> joinConditions();

        /// 返回用户 WHERE 条件表达式
        ///
        /// @return 用户 WHERE 条件表达式，可能为 null 或 true
        ExpressionNode whereCondition();

        /// 追加条件表达式到 SQL
        ///
        /// @param where 条件表达式
        void appendPredicate(ExpressionNode where);

        /// 追加 JOIN 表（表名 + 别名）到 SQL
        ///
        /// 用于 EXISTS 子查询中列出 JOIN 表
        ///
        /// @param delimiter 分隔符（第一个表为空，后续为 ", "）
        void appendJoinTable(String delimiter, JoinTableInfo tableInfo);

        /// 追加 JOIN 表的连接条件到 SQL
        ///
        /// 用于 EXISTS 子查询的 WHERE 部分
        ///
        /// @param delimiter 分隔符（第一个条件为空，后续为 " and "）
        void appendJoinCondition(String delimiter, int index);

        /// 检查条件是否为空或 true
        ///
        /// @param where 条件表达式
        /// @return 如果条件为空或 true 则返回 true
        default boolean isNullOrTrue(ExpressionNode where) {
            return ExpressionNodes.isNullOrTrue(where);
        }
    }

    /// JOIN 表信息
    ///
    /// 包含 JOIN 表名和别名
    interface JoinTableInfo {
        /// 返回 JOIN 表名（已引用）
        ///
        /// @return 表名
        String tableName();

        /// 返回 JOIN 表别名
        ///
        /// @return 表别名
        String tableAlias();
    }

    /// 默认 SQL 方言实例
    SqlDialect DEFAULT = new DefaultDialect();

    /// 可变方言注册列表
    ///
    /// 使用 CopyOnWriteArrayList 保证线程安全。
    /// 新方言通过 {@link #register(SqlDialect)} 方法注册到此列表，按优先级排序。
    List<SqlDialect> DIALECTS = new CopyOnWriteArrayList<>();

    /// 返回方言的优先级
    ///
    /// 优先级用于在检测时确定方言的匹配顺序。
    /// 数值越小优先级越高（优先匹配）。
    /// 默认优先级为 100。
    ///
    /// @return 优先级数值
    default int priority() {
        return 100;
    }

    /// 注册方言到检测列表（按优先级排序）
    ///
    /// 新方言在类加载时调用此方法注册自己：
    /// <pre>
    /// static {
    ///     SqlDialect.register(new OracleDialect());
    /// }
    /// </pre>
    ///
    /// 注册时会按优先级排序插入，优先级高的方言排在前面。
    ///
    /// @param dialect 要注册的方言
    static void register(SqlDialect dialect) {
        int priority = dialect.priority();
        int index = 0;
        for (SqlDialect existing : DIALECTS) {
            if (existing.priority() > priority) {
                break;
            }
            index++;
        }
        DIALECTS.add(index, dialect);
    }

    /// 判断此方言是否匹配给定的数据库元数据
    ///
    /// @param metaData 数据库元数据
    /// @return 如果匹配则返回 true
    /// @throws SQLException 如果访问元数据失败
    default boolean matches(DatabaseMetaData metaData) throws SQLException {
        return false;
    }

    /// 根据数据源自动检测 SQL 方言
    ///
    /// 遍历已注册的方言列表，返回第一个匹配的方言。
    /// 如无匹配则返回 DEFAULT。
    /// 如果方言列表为空，会先加载默认方言类以触发静态注册。
    ///
    /// @param dataSource 数据源
    /// @return 对应的 SQL 方言实例
    /// @throws SQLException 如果获取数据库连接失败
    static SqlDialect detectFromDataSource(DataSource dataSource) throws SQLException {
        // 如果方言列表为空，加载默认方言类以触发静态注册
        if (DIALECTS.isEmpty()) {
            loadDefaultDialects();
        }
        try (var connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            for (SqlDialect dialect : DIALECTS) {
                if (dialect.matches(metaData)) {
                    return dialect;
                }
            }
            return DEFAULT;
        }
    }

    /// 加载默认方言类以触发静态注册
    ///
    /// 通过 Class.forName() 加载方言类，触发其静态初始化块中的注册逻辑。
    /// 使用类引用获取名称，确保编译时检查。
    ///
    private static void loadDefaultDialects() {
        try {
            Class.forName(MySqlDialect.class.getName());
            Class.forName(PostgresqlDialect.class.getName());
            Class.forName(SqlServerDialect.class.getName());
            Class.forName(H2Dialect.class.getName());
        } catch (ClassNotFoundException _) {
        }
    }
}