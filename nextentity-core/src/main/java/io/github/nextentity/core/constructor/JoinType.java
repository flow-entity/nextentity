package io.github.nextentity.core.constructor;

/// Join 类型枚举
///
/// 定义 SQL JOIN 操作的类型，用于构建 FROM 子句。
///
/// @author HuangChengwei
/// @since 2.2.2
public enum JoinType {

    /// 左连接
    LEFT("LEFT"),

    /// 内连接
    INNER("INNER"),

    /// 右连接
    RIGHT("RIGHT"),

    /// 交叉连接
    CROSS("CROSS");

    private final String keyword;

    JoinType(String keyword) {
        this.keyword = keyword;
    }

    /// 获取 SQL 关键字
    ///
    /// @return SQL JOIN 关键字
    public String keyword() {
        return keyword;
    }
}