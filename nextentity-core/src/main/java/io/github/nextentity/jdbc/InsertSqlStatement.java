package io.github.nextentity.jdbc;

///
/// 插入SQL语句类
///
/// 该类封装了用于插入操作的SQL语句及其相关信息，扩展了BatchSqlStatement，
/// 增加了对实体集合和是否返回生成键的支持。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class InsertSqlStatement extends BatchSqlStatement implements SqlStatement {
    private final Iterable<?> entities;
    private final boolean returnGeneratedKeys;
    private final boolean batchInsert;

    /// 构造插入SQL语句对象
    ///
    /// @param entities            要插入的实体集合
    /// @param sql                 SQL语句字符串
    /// @param parameters          参数集合
    /// @param returnGeneratedKeys 是否返回生成的键
    public InsertSqlStatement(Iterable<?> entities,
                              String sql,
                              Iterable<? extends Iterable<?>> parameters,
                              boolean returnGeneratedKeys) {
        this(entities, sql, parameters, returnGeneratedKeys, true);
    }

    /// 构造插入SQL语句对象
    ///
    /// @param entities            要插入的实体集合
    /// @param sql                 SQL语句字符串
    /// @param parameters          参数集合
    /// @param returnGeneratedKeys 是否返回生成的键
    /// @param batchInsert         是否批量执行插入
    public InsertSqlStatement(Iterable<?> entities,
                              String sql,
                              Iterable<? extends Iterable<?>> parameters,
                              boolean returnGeneratedKeys,
                              boolean batchInsert) {
        super(sql, parameters);
        this.entities = entities;
        this.returnGeneratedKeys = returnGeneratedKeys;
        this.batchInsert = batchInsert;
    }

    /// 判断是否返回生成的键
    ///
    /// @return 如果需要返回生成的键则返回true，否则返回false
    public boolean returnGeneratedKeys() {
        return returnGeneratedKeys;
    }

    /// 判断是否批量执行插入
    ///
    /// @return 如果批量执行则返回true，逐条执行则返回false
    public boolean batchInsert() {
        return batchInsert;
    }

    /// 获取实体集合
    ///
    /// @return 要插入的实体集合
    public Iterable<?> entities() {
        return entities;
    }
}
