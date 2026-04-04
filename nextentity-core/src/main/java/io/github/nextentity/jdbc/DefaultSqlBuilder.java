package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.Iterators;
import org.jspecify.annotations.NonNull;

import java.util.*;

/// 统一的 SQL 构建器
///
/// 该类合并了查询和更新 SQL 构建功能，同时实现 QuerySqlBuilder 和 JdbcUpdateSqlBuilder 接口。
/// 通过 SqlDialect 支持所有数据库方言（MySQL、PostgreSQL、SQL Server），方言差异由 SqlDialect 处理。
///
/// @author HuangChengwei
/// @since 2.0
public class DefaultSqlBuilder implements SqlBuilder {

    private final SqlDialect dialect;

    /// 构造 SQL 构建器
    ///
    /// @param dialect SQL 方言
    public DefaultSqlBuilder(SqlDialect dialect) {
        this.dialect = dialect;
    }

    /// 构建查询 SQL 语句
    ///
    /// @param context 查询上下文
    /// @return 查询 SQL 语句对象
    @Override
    public QuerySqlStatement buildQueryStatement(QueryContext context) {
        return new QueryStatementBuilder(context, dialect).build();
    }

    /// 构建插入 SQL 语句
    ///
    /// @param entities   实体集合
    /// @param entityType 实体类型
    /// @return 插入 SQL 语句列表
    @Override
    public List<InsertSqlStatement> buildInsertStatement(Iterable<?> entities, @NonNull EntityType entityType) {
        EntityAttribute idAttribute = entityType.id();
        boolean hasNullId = false;
        for (Object entity : entities) {
            if (idAttribute.getDatabaseValue(entity) == null) {
                hasNullId = true;
                break;
            }
        }
        ImmutableArray<? extends EntityAttribute> selectList = entityType.getPrimitives();
        return Collections.singletonList(buildInsertStatement(entities, entityType, selectList, hasNullId));
    }

    /// 构建插入 SQL 语句
    ///
    /// @param entities    实体集合
    /// @param entityType  实体类型
    /// @param attributes  属性列表
    /// @param generateKey 是否生成键值
    /// @return 插入 SQL 语句对象
    protected InsertSqlStatement buildInsertStatement(Iterable<?> entities,
                                                      EntityType entityType,
                                                      Iterable<? extends EntityAttribute> attributes,
                                                      boolean generateKey) {
        String tableName = entityType.tableName();
        List<EntityAttribute> columns = new ArrayList<>();
        StringBuilder sql = new StringBuilder("insert into ")
                .append(leftTicks())
                .append(tableName)
                .append(rightTicks())
                .append(" (");
        String delimiter = "";
        for (EntityAttribute attribute : attributes) {
            sql.append(delimiter).append(leftTicks()).append(attribute.columnName()).append(rightTicks());
            columns.add(attribute);
            delimiter = ",";
        }
        sql.append(") values (");
        delimiter = "";
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            sql.append(delimiter).append("?");
            delimiter = ",";
        }
        sql.append(")");
        Iterable<? extends Iterable<?>> parameters = getParameters(entities, attributes);
        return new InsertSqlStatement(entities, sql.toString(), parameters, generateKey);
    }

    /// 构建更新 SQL 语句
    ///
    /// @param entities   实体集合
    /// @param entityType 实体类型
    /// @return 批量 SQL 语句对象
    @Override
    public BatchSqlStatement buildUpdateStatement(Iterable<?> entities,
                                                  EntitySchema entityType) {
        ImmutableArray<? extends EntityAttribute> columns = entityType.getPrimitives();
        StringBuilder sql = new StringBuilder("update ")
                .append(leftTicks())
                .append(entityType.tableName())
                .append(rightTicks())
                .append(" set ");
        EntityAttribute id = entityType.id();
        String delimiter = "";
        List<EntityAttribute> paramAttr = new ArrayList<>(columns.size() + 1);
        EntityAttribute version = entityType.version();
        for (EntityAttribute attribute : columns) {
            if (Objects.equals(entityType.id(), attribute) || !attribute.isUpdatable()) {
                continue;
            }
            sql.append(delimiter);
            delimiter = ",";
            sql.append(leftTicks()).append(attribute.columnName()).append(rightTicks()).append("=");

            if (attribute == version) {
                sql.append("?+1");
            } else {
                sql.append("?");
            }
            paramAttr.add(attribute);

        }
        sql.append(" where ").append(leftTicks()).append(id.columnName()).append(rightTicks()).append("=?");
        paramAttr.add(id);
        if (version != null) {
            sql.append(" and ")
                    .append(leftTicks())
                    .append(version.columnName())
                    .append(rightTicks())
                    .append("=?");
            paramAttr.add(version);
        }
        return new BatchSqlStatement(sql.toString(), getParameters(entities, paramAttr));
    }

    /// 构建删除 SQL 语句
    ///
    /// @param entities 实体集合
    /// @param entity   实体类型
    /// @return 批量 SQL 语句对象
    @Override
    public BatchSqlStatement buildDeleteStatement(Iterable<?> entities, EntityType entity) {
        EntityAttribute id = entity.id();
        String sql = "delete from " + leftTicks() + entity.tableName() + rightTicks()
                     + " where " + leftTicks() + id.columnName() + rightTicks() + "=?";
        List<EntityAttribute> paramAttr = Collections.singletonList(id);
        Iterable<? extends Iterable<?>> parameters = getParameters(entities, paramAttr);
        return new BatchSqlStatement(sql, parameters);
    }

    /// 构建条件更新 SQL 语句
    ///
    /// @param entity         实体类型
    /// @param metamodel      实体元模型
    /// @param setValues      set 子句的列名和值映射
    /// @param whereCondition where 条件表达式
    /// @return 更新 SQL 语句对象
    @Override
    public UpdateSqlStatement buildConditionalUpdateStatement(EntityType entity,
                                                              Metamodel metamodel,
                                                              Map<String, Object> setValues,
                                                              ExpressionNode whereCondition) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("update ");
        sql.append(leftTicks()).append(entity.tableName()).append(rightTicks());
        sql.append(" set ");

        String delimiter = "";
        for (Map.Entry<String, Object> entry : setValues.entrySet()) {
            sql.append(delimiter);
            sql.append(leftTicks()).append(entry.getKey()).append(rightTicks());
            sql.append(" = ?");
            params.add(entry.getValue());
            delimiter = ", ";
        }

        if (whereCondition != null) {
            sql.append(" where ");
            appendWhereCondition(sql, params, whereCondition, entity, metamodel);
        }

        return new UpdateSqlStatement(sql.toString(), params);
    }

    /// 构建条件删除 SQL 语句
    ///
    /// @param entity         实体类型
    /// @param metamodel      实体元模型
    /// @param whereCondition where 条件表达式
    /// @return 删除 SQL 语句对象
    @Override
    public DeleteSqlStatement buildConditionalDeleteStatement(EntityType entity,
                                                              Metamodel metamodel,
                                                              ExpressionNode whereCondition) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("delete from ");
        sql.append(leftTicks()).append(entity.tableName()).append(rightTicks());

        if (whereCondition != null) {
            sql.append(" where ");
            appendWhereCondition(sql, params, whereCondition, entity, metamodel);
        }

        return new DeleteSqlStatement(sql.toString(), params);
    }

    // ==================== 辅助方法 ====================

    protected String leftTicks() {
        return dialect.leftQuotedIdentifier();
    }

    protected String rightTicks() {
        return dialect.rightQuotedIdentifier();
    }

    /// 获取参数列表
    ///
    /// @param entities   实体集合
    /// @param attributes 属性列表
    /// @return 参数列表
    private static Iterable<? extends Iterable<?>> getParameters(Iterable<?> entities,
                                                                 Iterable<? extends EntityAttribute> attributes) {
        return Iterators.map(entities, entity -> Iterators.map(attributes, attr -> {
            Object value = attr.getDatabaseValue(entity);
            if (attr.isVersion() && value == null) {
                value = 0;
            }
            return value == null ? new NullParameter(attr.type()) : value;
        }));
    }

    /// 处理 where 条件表达式
    protected void appendWhereCondition(StringBuilder sql,
                                        List<Object> params,
                                        ExpressionNode node,
                                        EntityType entity,
                                        Metamodel metamodel) {
        if (node instanceof PathNode pathNode) {
            appendPath(sql, pathNode, entity);
        } else if (node instanceof LiteralNode(Object value)) {
            sql.append("?");
            params.add(value);
        } else if (node instanceof OperatorNode operatorNode) {
            appendOperatorNode(sql, params, operatorNode, entity, metamodel);
        }
    }

    /// 处理操作符节点
    private void appendOperatorNode(StringBuilder sql,
                                    List<Object> params,
                                    OperatorNode node,
                                    EntityType entity,
                                    Metamodel metamodel) {
        Operator operator = node.operator();
        List<? extends ExpressionNode> operands = node.operands();

        switch (operator) {
            case AND, OR -> {
                sql.append("(");
                for (int i = 0; i < operands.size(); i++) {
                    if (i > 0) {
                        sql.append(" ").append(operator.sign()).append(" ");
                    }
                    appendWhereCondition(sql, params, operands.get(i), entity, metamodel);
                }
                sql.append(")");
            }
            case NOT -> {
                sql.append("not ");
                appendWhereCondition(sql, params, operands.getFirst(), entity, metamodel);
            }
            case EQ, NE, GT, GE, LT, LE, LIKE -> {
                ExpressionNode leftOperand = operands.get(0);
                ExpressionNode rightOperand = operands.get(1);

                // 检查是否是嵌套路径的比较操作
                if (leftOperand instanceof PathNode pathNode && pathNode.deep() > 1) {
                    // 嵌套路径：生成子查询
                    appendNestedPathComparison(sql, params, pathNode, operator, rightOperand, entity, metamodel);
                } else {
                    // 简单路径：直接比较
                    appendWhereCondition(sql, params, leftOperand, entity, metamodel);
                    sql.append(" ").append(operator.sign()).append(" ");
                    appendWhereCondition(sql, params, rightOperand, entity, metamodel);
                }
            }
            case IN -> {
                ExpressionNode leftOperand = operands.getFirst();

                // 检查是否是嵌套路径的 IN 操作
                if (leftOperand instanceof PathNode pathNode && pathNode.deep() > 1) {
                    // 嵌套路径：生成子查询
                    appendNestedPathIn(sql, params, pathNode, operands, entity, metamodel);
                } else {
                    // 简单路径：直接处理
                    appendWhereCondition(sql, params, leftOperand, entity, metamodel);
                    appendIn(sql, params, entity, metamodel, operands);
                }
            }
            case IS_NULL -> {
                appendWhereCondition(sql, params, operands.getFirst(), entity, metamodel);
                sql.append(" is null");
            }
            case IS_NOT_NULL -> {
                appendWhereCondition(sql, params, operands.getFirst(), entity, metamodel);
                sql.append(" is not null");
            }
            case BETWEEN -> {
                appendWhereCondition(sql, params, operands.get(0), entity, metamodel);
                sql.append(" between ");
                appendWhereCondition(sql, params, operands.get(1), entity, metamodel);
                sql.append(" and ");
                appendWhereCondition(sql, params, operands.get(2), entity, metamodel);
            }
            default -> throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
    }

    private void appendIn(StringBuilder sql,
                          List<Object> params,
                          EntityType entity,
                          Metamodel metamodel,
                          List<? extends ExpressionNode> operands) {
        sql.append(" IN (");
        for (int i = 1; i < operands.size(); i++) {
            if (i > 1) sql.append(", ");
            appendWhereCondition(sql, params, operands.get(i), entity, metamodel);
        }
        sql.append(")");
    }

    /// 嵌套路径的 Join 信息
    protected record JoinInfo(
            String foreignKeyColumn,
            String joinTableName,
            String referencedColumnName,
            String targetColumn,
            EntityAttribute entityAttribute
    ) {
    }

    /// 获取嵌套路径的 Join 信息
    protected JoinInfo getJoinInfo(PathNode nestedPath, EntityType entity) {
        Attribute attr = entity.getAttribute(nestedPath);
        if (!(attr instanceof EntityAttribute entityAttribute)) {
            throw new UnsupportedOperationException("Unsupported nested path: " + nestedPath);
        }

        Schema declareBy = entityAttribute.declareBy();
        if (!(declareBy instanceof JoinAttribute joinAttribute)) {
            return null; // 不是 Join，返回 null 表示回退到简单路径
        }

        String foreignKeyColumn = joinAttribute.joinName();
        String joinTableName = joinAttribute.tableName();
        String referencedColumnName = joinAttribute.referencedColumnName();
        if (referencedColumnName == null || referencedColumnName.isEmpty()) {
            referencedColumnName = joinAttribute.id().columnName();
        }
        String targetColumn = entityAttribute.columnName();

        return new JoinInfo(foreignKeyColumn, joinTableName, referencedColumnName, targetColumn, entityAttribute);
    }

    /// 构建嵌套路径子查询的前缀部分
    private void appendNestedPathSubQueryPrefix(StringBuilder sql, JoinInfo joinInfo) {
        sql.append(leftTicks()).append(joinInfo.foreignKeyColumn()).append(rightTicks());
        sql.append(" IN (SELECT ");
        sql.append(leftTicks()).append(joinInfo.referencedColumnName()).append(rightTicks());
        sql.append(" FROM ");
        sql.append(leftTicks()).append(joinInfo.joinTableName()).append(rightTicks());
        sql.append(" where ");
        sql.append(leftTicks()).append(joinInfo.targetColumn()).append(rightTicks());
    }

    /// 为嵌套路径的比较操作生成子查询
    private void appendNestedPathComparison(StringBuilder sql, List<Object> params,
                                            PathNode nestedPath, Operator operator,
                                            ExpressionNode rightOperand, EntityType entity, Metamodel metamodel) {
        JoinInfo joinInfo = getJoinInfo(nestedPath, entity);

        if (joinInfo != null) {
            // 构建子查询
            appendNestedPathSubQueryPrefix(sql, joinInfo);
            sql.append(" ").append(operator.sign()).append(" ");
            appendWhereCondition(sql, params, rightOperand, entity, metamodel);
            sql.append(")");
        } else {
            // 回退到简单路径处理
            Attribute attr = entity.getAttribute(nestedPath);
            EntityAttribute entityAttribute = (EntityAttribute) attr;
            sql.append(leftTicks()).append(entityAttribute.columnName()).append(rightTicks());
            sql.append(" ").append(operator.sign()).append(" ");
            appendWhereCondition(sql, params, rightOperand, entity, metamodel);
        }
    }

    /// 为嵌套路径的 IN 操作生成子查询
    private void appendNestedPathIn(StringBuilder sql, List<Object> params,
                                    PathNode nestedPath, List<? extends ExpressionNode> operands,
                                    EntityType entity, Metamodel metamodel) {
        JoinInfo joinInfo = getJoinInfo(nestedPath, entity);

        if (joinInfo != null) {
            // 构建子查询
            appendNestedPathSubQueryPrefix(sql, joinInfo);
            sql.append(" IN (");
            for (int i = 1; i < operands.size(); i++) {
                if (i > 1) sql.append(", ");
                appendWhereCondition(sql, params, operands.get(i), entity, metamodel);
            }
            sql.append("))");
        } else {
            // 回退到简单路径处理
            Attribute attr = entity.getAttribute(nestedPath);
            EntityAttribute entityAttribute = (EntityAttribute) attr;
            sql.append(leftTicks()).append(entityAttribute.columnName()).append(rightTicks());
            appendIn(sql, params, entity, metamodel, operands);
        }
    }

    /// 处理路径节点
    protected void appendPath(StringBuilder sql, PathNode path, EntityType entity) {
        EntityAttribute attribute = (EntityAttribute) entity.getAttribute(path);
        sql.append(leftTicks()).append(attribute.columnName()).append(rightTicks());
    }

}