package io.github.nextentity.jdbc;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.jdbc.JdbcQueryExecutor.ResultCollector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

///
/// JDBC结果收集器实现
///
/// 该类负责处理从数据库查询返回的结果集，将其转换为对应的目标对象列表。
/// 它实现了ResultCollector接口，提供具体的解析逻辑。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class JdbcResultCollector implements ResultCollector {

    /// 构造JDBC结果收集器
    public JdbcResultCollector() {
    }


    /// 解析结果集
    ///
    /// @param <T> 结果类型
    /// @param resultSet 结果集
    /// @param context 查询上下文
    /// @return 解析后的结果列表
    /// @throws SQLException SQL异常
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
        // 查询完成后设置结果列表（触发后处理）
        context.setResults(result);
        return TypeCastUtil.cast(result);
    }


}
