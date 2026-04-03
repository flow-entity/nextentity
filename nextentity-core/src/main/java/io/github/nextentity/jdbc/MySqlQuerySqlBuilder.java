package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.jdbc.JdbcQueryExecutor.QuerySqlBuilder;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

///
/// MySQL查询SQL构建器
///
/// 该类专门用于为MySQL数据库构建查询SQL语句，处理MySQL特有的语法和标识符引用规则。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class MySqlQuerySqlBuilder implements QuerySqlBuilder {

    /// 构建查询SQL语句
    ///
    /// @param context 查询上下文
    /// @return 查询SQL语句对象
    @Override
    public QuerySqlStatement build(QueryContext context) {
        return new Builder(context).build();
    }

    /// MySQL查询SQL构建器内部实现类
    ///
    /// 该类扩展了抽象查询SQL构建器，实现了MySQL特定的SQL语法特性。
    static class Builder extends AbstractQuerySqlBuilder {

        /// 构造函数
        ///
        /// @param sql SQL语句字符串构建器
        /// @param args 参数列表
        /// @param context 查询上下文
        /// @param selectIndex 选择索引计数器
        /// @param subIndex 子查询索引
        public Builder(StringBuilder sql,
                       List<Object> args,
                       QueryContext context,
                       AtomicInteger selectIndex,
                       int subIndex) {
            super(sql, args, context, selectIndex, subIndex);
        }


        /// 构造函数
        ///
        /// @param context 查询上下文
        public Builder(QueryContext context) {
            super(context);
        }

        /// 获取左侧标识符引用字符
        ///
        /// @return 左侧标识符引用字符，MySQL使用反引号
        @Override
        protected String leftQuotedIdentifier() {
            return "`";
        }

        /// 获取右侧标识符引用字符
        ///
        /// @return 右侧标识符引用字符，MySQL使用反引号
        @Override
        protected String rightQuotedIdentifier() {
            return "`";
        }

        /// 追加子查询结构
        ///
        /// @param subContext 子查询上下文
        @Override
        protected void appendQueryStructure(QueryContext subContext) {
            new Builder(sql, args, subContext, selectIndex, subIndex + 1).doBuilder();
        }

        /// 追加谓词表达式
        ///
        /// @param node 表达式节点
        @Override
        protected void appendPredicate(ExpressionNode node) {
            appendExpression(node);
        }

        /// 追加偏移量和限制条件
        ///
        /// MySQL使用LIMIT子句指定偏移量和限制条件
        @Override
        protected void appendOffsetAndLimit() {
            int offset = unwrap(context.getStructure().offset());
            int limit = unwrap(context.getStructure().limit());
            if (offset > 0) {
                sql.append(" limit ?,?");
                args.add(offset);
                args.add(limit < 0 ? Long.MAX_VALUE : limit);
            } else if (limit >= 0) {
                sql.append(" limit ?");
                args.add(limit);
            }
        }

        /// 解包整数值
        ///
        /// @param integer 整数包装对象
        /// @return 基本整数值，如果输入为null则返回-1
        private static int unwrap(Integer integer) {
            return integer == null ? -1 : integer;
        }
    }

}
