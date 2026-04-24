package io.github.nextentity.jdbc;

import io.github.nextentity.api.SortOrder;
import io.github.nextentity.core.constructor.QueryContext;
import io.github.nextentity.core.constructor.Column;
import io.github.nextentity.core.constructor.ValueConstructor;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.util.ImmutableArray;
import jakarta.persistence.LockModeType;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/// 查询 SQL 构建器实现类
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class QueryStatementBuilder extends AbstractStatementBuilder {

    protected static final String NONE_DELIMITER = "";
    protected static final String DELIMITER = ",";
    protected static final String SELECT = "select ";
    protected static final String DISTINCT = "distinct ";
    protected static final String HAVING = " having ";
    protected static final String ORDER_BY = " order by ";
    protected static final String DESC = "desc";
    protected static final String ASC = "asc";

    protected final QueryContext context;
    protected final ValueConstructor constructor;


    protected QueryStatementBuilder(StringBuilder sql,
                                    List<Object> args,
                                    QueryContext context,
                                    SqlDialect dialect,
                                    JdbcConfig config,
                                    AtomicInteger selectIndex,
                                    int subIndex) {
        super(sql, args, dialect, config, selectIndex, subIndex, getFromAlias(context, subIndex));
        this.context = context;
        this.constructor = context.newConstructor();
    }

    private static String getFromAlias(QueryContext context, int subIndex) {
        final String fromAlias;
        String prefix;
        From from = context.getStructure().from();
        if (from instanceof FromEntity(Class<?> type)) {
            prefix = shortAlias(type.getSimpleName());
        } else {
            prefix = "t";
        }
        fromAlias = subIndex == 0 ? prefix + "_" : prefix + subIndex + "_";
        return fromAlias;
    }

    public QueryStatementBuilder(QueryContext context, SqlDialect dialect, JdbcConfig config) {
        this(new StringBuilder(), new ArrayList<>(), context, dialect, config, new AtomicInteger(), 0);
    }

    protected QuerySqlStatement build() {
        doBuilder();
        return new QuerySqlStatement(sql.toString(), args);
    }

    protected void doBuilder() {
        initJoinColumnIndex();
        appendSelect();
        appendFrom();
        appendJoin();
        appendWhere();
        appendGroupBy();
        appendHaving();
        appendOrderBy();
        appendOffsetAndLimit();
        appendLockModeType(context.getStructure().lockType());
    }

    protected void appendSelect() {
        sql.append(SELECT);
        if (context.getStructure().select().distinct()) {
            sql.append(DISTINCT);
        }
        String join = NONE_DELIMITER;
        int columnIndex = 0;
        for (Column expression : constructor.columns()) {
            sql.append(join);
            appendExpression(expression);
            appendSelectAlias(expression, columnIndex++);
            join = DELIMITER;
        }
    }

    protected void appendSelectAlias(Column expression, int columnIndex) {
        if (dialect.requiresAliasForAggregateColumns()
            && expression.isOperatorNode()) {
            sql.append(" as col_").append(columnIndex);
        }
    }

    protected void appendLockModeType(LockModeType lockModeType) {
        if (lockModeType == null) {
            return;
        }
        // For databases that support FOR UPDATE syntax, append at end
        // For SQL Server, lock hints are appended after FROM clause
        if (dialect.supportsForUpdateSyntax()) {
            dialect.appendLockMode(sql, lockModeType);
        }
    }

    protected void appendFrom() {
        appendBlank().append(FROM);
        From from = context.getStructure().from();
        if (from instanceof FromEntity) {
            appendFromTable();
        } else if (from instanceof FromSubQuery) {
            appendExpression(((FromSubQuery) from).structure());
        }
        appendFromAlias();
        // For SQL Server, lock hints go after the table alias in FROM clause
        LockModeType lockModeType = context.getStructure().lockType();
        if (lockModeType != null && !dialect.supportsForUpdateSyntax()) {
            dialect.appendLockMode(sql, lockModeType);
        }
    }

    @Override
    protected QueryContext newContext(QueryStructure queryStructure) {
        return context.newContext(queryStructure);
    }

    @Override
    protected EntityType getEntityType() {
        return context.getEntityType();
    }

    @Override
    protected ExpressionNode where() {
        return context.getStructure().where();
    }

    protected void appendHaving() {
        ExpressionNode having = context.getStructure().having();
        if (ExpressionNodes.isNullOrTrue(having)) {
            return;
        }
        sql.append(HAVING);
        appendPredicate(having);
    }

    private void initJoinColumnIndex() {
        QueryStructure structure = context.getStructure();
        addJoinPrimitive(constructor.columns());
        addJoin(structure.where());
        addJoin(structure.groupBy());
        for (SortExpression order : structure.orderBy()) {
            addJoin(order.expression());
        }
        addJoin(structure.having());
    }

    protected void appendOffsetAndLimit() {
        QueryStructure queryStructure = context.getStructure();
        int offset = unwrap(queryStructure.offset());
        int limit = unwrap(queryStructure.limit());

        // SQL Server requires ORDER BY before OFFSET clause
        if (dialect.requiresOrderByForPagination() && (offset > 0 || limit >= 0)) {
            if (queryStructure.orderBy() == null || queryStructure.orderBy().isEmpty()) {
                if (queryStructure.select().distinct()) {
                    sql.append(" order by 1");
                } else {
                    sql.append(" order by (select 0)");
                }
            }
        }

        dialect.appendLimitOffset(sql, args, offset, limit);
    }

    private static int unwrap(Integer integer) {
        return integer == null ? -1 : integer;
    }

    protected void appendGroupBy() {
        List<? extends ExpressionNode> groupBy = context.getStructure().groupBy();
        if (groupBy != null && !groupBy.isEmpty()) {
            sql.append(" group by ");
            boolean first = true;
            for (ExpressionNode e : groupBy) {
                if (first) {
                    first = false;
                } else {
                    sql.append(",");
                }
                appendExpression(e);
            }
        }
    }

    protected void appendOrderBy() {
        List<? extends SortExpression> orders = context.getStructure().orderBy();
        if (orders != null && !orders.isEmpty()) {
            sql.append(ORDER_BY);
            String delimiter = "";
            for (SortExpression order : orders) {
                sql.append(delimiter);
                delimiter = ",";
                int selectIndex = getSelectIndex(order);
                if (selectIndex > 0) {
                    sql.append(selectIndex + 1);
                } else {
                    appendExpression(order.expression());
                }
                sql.append(" ").append(order.order() == SortOrder.DESC ? DESC : ASC);
            }

        }
    }

    private int getSelectIndex(SortExpression order) {
        List<Column> primitives = constructor.columns();
        for (int i = 0; i < primitives.size(); i++) {
            Column primitive = primitives.get(i);
            if (primitive.source().equals(order.expression())) {
                return i;
            }
        }
        return -1;
    }

}