package io.github.nextentity.jdbc;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.jdbc.JdbcQueryExecutor.ResultCollector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcResultCollector implements ResultCollector {

    public JdbcResultCollector() {
    }


    @Override
    public <T> List<T> resolve(ResultSet resultSet, QueryContext context) throws SQLException {
        int type = resultSet.getType();
        List<Object> result;
        if (type != ResultSet.TYPE_FORWARD_ONLY) {
            resultSet.last();
            int size = resultSet.getRow();
            result = new ArrayList<>(size);
            resultSet.beforeFirst();
        } else {
            result = new ArrayList<>();
        }
        int columnsCount = resultSet.getMetaData().getColumnCount();
        ImmutableArray<SelectItem> primitives = context.getSelectedExpression();
        if (primitives.size() != columnsCount) {
            throw new IllegalStateException(
                    String.format("Column count mismatch: expected %d (from query projection), actual %d (from ResultSet). " +
                                    "This usually indicates a mismatch between the SELECT clause and the result mapping.",
                            primitives.size(), columnsCount));
        }
        while (resultSet.next()) {
            JdbcArguments arguments = new JdbcArguments(resultSet);
            Object o = context.construct(arguments);
            result.add(o);
        }
        return TypeCastUtil.cast(result);
    }


}
