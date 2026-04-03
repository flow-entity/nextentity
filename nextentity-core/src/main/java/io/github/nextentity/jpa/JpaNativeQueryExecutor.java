package io.github.nextentity.jpa;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.jdbc.JdbcQueryExecutor.QuerySqlBuilder;
import io.github.nextentity.jdbc.QueryContext;
import io.github.nextentity.jdbc.QuerySqlStatement;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

///
/// JPA 原生查询执行器，用于执行原生 SQL 查询并处理结果集。
/// 该执行器将查询结构转换为原生 SQL 并通过 JPA EntityManager 执行。
///
/// @author HuangChengwei
/// @since 2.1
public class JpaNativeQueryExecutor implements QueryExecutor {
    private final QuerySqlBuilder sqlBuilder;
    private final EntityManager entityManager;
    private final Metamodel metamodel;

    public JpaNativeQueryExecutor(QuerySqlBuilder sqlBuilder,
                                  EntityManager entityManager,
                                  Metamodel metamodel) {
        this.sqlBuilder = sqlBuilder;
        this.entityManager = entityManager;
        this.metamodel = metamodel;
    }

    @Override
    public <T> List<T> getList(@NonNull QueryStructure queryStructure) {
        return queryByNativeSql(queryStructure);
    }

    private <T> List<T> queryByNativeSql(@NonNull QueryStructure queryStructure) {
        QueryContext context = QueryContext.create(queryStructure, metamodel, true);
        QuerySqlStatement preparedSql = sqlBuilder.build(context);
        jakarta.persistence.Query query = entityManager.createNativeQuery(preparedSql.sql());
        int position = 0;
        for (Object arg : preparedSql.parameters()) {
            query.setParameter(++position, arg);
        }
        List<?> list = TypeCastUtil.cast(query.getResultList());

        return resolve(list, context);
    }

    protected <T> List<T> resolve(
            List<?> resultSet,
            QueryContext context) {
        List<Object> result = new ArrayList<>(resultSet.size());
        if (resultSet.isEmpty()) {
            return TypeCastUtil.cast(result);
        }
        Object first = resultSet.get(0);
        int columnsCount = asArray(first).length;
        ImmutableArray<SelectItem> expressions = context.getSelectedExpression();
        if (expressions.size() != columnsCount) {
            throw new IllegalStateException(
                    String.format("Column count mismatch: expected %d (from query projection), actual %d (from query result). " +
                                    "This usually indicates a mismatch between the SELECT clause and the result mapping.",
                            expressions.size(), columnsCount));
        }

        for (Object o : resultSet) {
            Object[] array = asArray(o);
            JpaArguments arguments = new JpaArguments(array);
            Object row = context.construct(arguments);
            result.add(row);
        }
        return TypeCastUtil.cast(result);
    }

    private Object[] asArray(Object r) {
        if (r instanceof Object[]) {
            return (Object[]) r;
        }
        return new Object[]{r};
    }
}
