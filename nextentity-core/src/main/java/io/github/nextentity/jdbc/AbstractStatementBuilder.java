package io.github.nextentity.jdbc;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.constructor.SelectItem;
import io.github.nextentity.core.constructor.QueryContext;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.meta.impl.IdentityValueConverter;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/// 查询 SQL 构建器实现类
///
/// @author HuangChengwei
/// @since 1.0.0
///
public abstract class AbstractStatementBuilder {

    protected static final String FROM = "from ";
    protected static final String WHERE = " where ";
    protected static final String LEFT_JOIN = " left join ";
    protected static final String ON = " on ";
    protected static final String USING = " using ";
    protected static final String AND = " and ";

    protected final StringBuilder sql;
    protected final List<Object> args;
    protected final Map<JoinAttribute, Integer> joins = new LinkedHashMap<>();

    protected final SqlDialect dialect;
    protected final JdbcConfig config;

    protected final String fromAlias;
    protected final int subIndex;
    protected final AtomicInteger selectIndex;

    public AbstractStatementBuilder(SqlDialect dialect, JdbcConfig config) {
        this(new StringBuilder(), new ArrayList<>(), dialect, config, new AtomicInteger(), 0, "t_");
    }

    protected AbstractStatementBuilder(StringBuilder sql,
                                       List<Object> args,
                                       SqlDialect dialect,
                                       JdbcConfig config,
                                       AtomicInteger selectIndex,
                                       int subIndex,
                                       String fromAlias) {
        this.sql = sql;
        this.args = args;
        this.subIndex = subIndex;
        this.selectIndex = selectIndex;
        this.dialect = dialect;
        this.config = config;
        this.fromAlias = fromAlias;
    }


    protected String leftQuotedIdentifier() {
        return dialect.leftQuotedIdentifier();
    }

    protected String rightQuotedIdentifier() {
        return dialect.rightQuotedIdentifier();
    }


    protected void appendSubQuery(QueryStructure queryStructure) {
        sql.append('(');
        QueryContext ctx = newContext(queryStructure);
        appendQueryStructure(ctx);
        sql.append(')');
    }

    protected abstract QueryContext newContext(QueryStructure queryStructure);

    protected void appendQueryStructure(QueryContext subContext) {
        new QueryStatementBuilder(sql, args, subContext, dialect, config, selectIndex, subIndex + 1).doBuilder();
    }

    protected void appendFromTable() {
        appendTable(sql, getEntityType());
        sql.append(" ");
    }

    protected abstract EntityType getEntityType();

    protected StringBuilder appendFromAlias() {
        return appendFromAlias(sql);
    }

    protected StringBuilder appendFromAlias(StringBuilder sql) {
        return sql.append(fromAlias);
    }

    @NonNull
    protected static String shortAlias(String symbol) {
        return String.valueOf(Character.toLowerCase(symbol.charAt(0)));
    }

    protected StringBuilder appendBlank() {
        return appendBlank(sql);
    }

    protected StringBuilder appendBlank(StringBuilder sql) {
        // noinspection SizeReplaceableByIsEmpty
        return sql.length() == 0 || " (,+-*/%=><".indexOf(sql.charAt(sql.length() - 1)) >= 0 ? sql : sql.append(' ');
    }

    protected void appendWhere() {
        ExpressionNode where = where();
        if (ExpressionNodes.isNullOrTrue(where)) {
            return;
        }
        sql.append(WHERE);
        appendPredicate(where);
    }

    protected abstract ExpressionNode where();

    protected void appendPredicate(ExpressionNode node) {
        if (node instanceof PathNode || node instanceof LiteralNode) {
            node = node.operate(Operator.EQ, LiteralNode.TRUE);
        }
        appendExpression(node);
    }

    protected void appendExpression(SelectItem column) {
        switch (column) {
            case SelectItem.Joined(var attribute, var join) -> {
                Integer index = joins.get(join);
                appendTableAlias(join, index).append('.')
                        .append(leftQuotedIdentifier())
                        .append(attribute.columnName())
                        .append(rightQuotedIdentifier());
            }
            case SelectItem.Expr(var source, var _) -> {
                if (source instanceof PathNode pathNode) {
                    appendAttribute(getEntityType().getAttribute(pathNode));
                } else {
                    appendExpression(source);
                }
            }
            case null -> throw new IllegalArgumentException("Column must not be null");
            default -> throw new IllegalArgumentException("Unsupported Column type: " + column.getClass().getName());
        }
    }

    protected void appendExpression(ExpressionNode expression) {
        switch (expression) {
            case LiteralNode literalNode -> appendLiteral(literalNode);
            case PathNode strings -> appendPaths(strings);
            case OperatorNode operatorNode -> appendOperation(operatorNode);
            case QueryStructure queryStructure -> appendSubQuery(queryStructure);
            default -> throw new UnsupportedOperationException("unknown type " + expression.getClass());
        }
    }

    protected void appendLiteral(LiteralNode literal) {
        Object value = literal.value();
        appendLiteralValue(value);
    }

    protected void appendLiteralValue(Object value) {
        // 应用 inlineNumericLiterals 配置
        if (inlineNumericLiterals(value)) {
            appendBlank().append(value);
        } else {
            appendBlank().append('?');
            this.args.add(value);
        }
    }

    protected boolean inlineNumericLiterals(Object value) {
        return config.inlineNumericLiterals() && (
                value instanceof Integer
                || value instanceof Long
                || value instanceof Short
                || value instanceof Byte
        );
    }

    protected void appendOperation(OperatorNode operation) {
        Operator operator = operation.operator();
        switch (operator) {
            case NOT: {
                appendNotOperation(operation);
                break;
            }
            case AND:
            case OR: {
                appendLogicalOperation(operation);
                break;
            }
            case LIKE:
            case GT:
            case EQ:
            case NE:
            case GE:
            case LT:
            case LE: {
                appendBinaryOperation(operation);
                break;
            }
            case ADD:
            case SUBTRACT:
            case MULTIPLY:
            case DIVIDE:
            case MOD: {
                appendMultiOperation(operation);
                break;
            }
            case LENGTH:
            case LOWER:
            case UPPER:
            case SUBSTRING:
            case TRIM:
            case NULLIF:
            case IF_NULL:
            case MIN:
            case MAX:
            case COUNT:
            case AVG:
            case SUM: {
                appendFunctionOperation(operation);
                break;
            }
            case IN: {
                appendIn(operation);
                break;
            }
            case BETWEEN: {
                appendBetween(operation);
                break;
            }
            case IS_NULL:
            case IS_NOT_NULL: {
                appendNullAssertion(operation);
                break;
            }
            case DISTINCT:
                appendPrepositionOperation(operation);
                break;
            default:
                throw new UnsupportedOperationException("unknown operator " + operator);
        }
    }

    protected void appendNullAssertion(OperatorNode operation) {
        appendFirstOperation(operation);
        appendBlank();
        appendOperator(operation.operator());
    }

    protected void appendPrepositionOperation(OperatorNode operation) {
        ExpressionNode operand = operation.firstOperand();
        Operator operator = operation.operator();
        appendOperator(operator);
        appendExpressionPriority(operand, operator);
    }

    protected void appendFirstOperation(OperatorNode operation) {
        ExpressionNode leftOperand = operation.firstOperand();
        Operator operator = operation.operator();
        appendExpressionPriority(leftOperand, operator);
    }

    protected void appendIn(OperatorNode operation) {
        ExpressionNode leftOperand = operation.firstOperand();
        ValueConverter<?, ?> convertor = getValueConverter(leftOperand);
        Operator operator = operation.operator();
        List<? extends ExpressionNode> operands = operation.operands();
        if (operands.size() <= 1) {
            appendBlank().append(0);
        } else {
            appendBlank();
            appendExpression(leftOperand);
            appendOperator(operator);
            boolean notSingleSubQuery = operands.size() != 2 || !(operands.get(1) instanceof QueryStructure);
            char join = notSingleSubQuery ? '(' : ' ';
            for (int i = 1; i < operands.size(); i++) {
                ExpressionNode expression = convertLiteralNode(convertor, operands.get(i));
                sql.append(join);
                appendExpression(expression);
                join = ',';
            }
            if (notSingleSubQuery) {
                sql.append(")");
            }
        }
    }

    protected ValueConverter<?, ?> getValueConverter(ExpressionNode leftOperand) {
        if (leftOperand instanceof PathNode pathNode) {
            MetamodelAttribute attribute = getEntityType().getAttribute(pathNode);
            if (attribute instanceof EntityBasicAttribute columnAttribute) {
                return columnAttribute.valueConvertor();
            }
        }
        return new IdentityValueConverter<>(Object.class);
    }

    protected ExpressionNode convertLiteralNode(ValueConverter<?, ?> convertor, ExpressionNode node) {
        if (!(node instanceof LiteralNode literalNode)) {
            return node;
        }

        Object value = literalNode.value();
        Object convertedValue = convertor.convertToDatabaseColumn(TypeCastUtil.unsafeCast(value));
        if (convertedValue != value) {
            return new LiteralNode(convertedValue);
        } else {
            return literalNode;
        }
    }

    protected void appendFunctionOperation(OperatorNode operation) {
        ExpressionNode leftOperand = operation.firstOperand();
        appendOperator(operation.operator());
        List<? extends ExpressionNode> operands = operation.operands();
        boolean notSingleSubQuery = !(leftOperand instanceof QueryStructure) || operands.size() != 1;
        if (notSingleSubQuery) {
            sql.append('(');
        }
        appendExpression(leftOperand);
        for (int i = 1; i < operands.size(); i++) {
            ExpressionNode expression = operands.get(i);
            sql.append(',');
            appendExpression(expression);
        }
        if (notSingleSubQuery) {
            sql.append(")");
        }
    }

    protected void appendNotOperation(OperatorNode operation) {
        ExpressionNode operand = operation.firstOperand();
        // SQL Server: NOT path → path = false
        if (dialect.shouldConvertNotToEqFalse()
            && (operand instanceof PathNode || operand instanceof LiteralNode)) {
            appendBinaryOperation(operand, Operator.EQ, LiteralNode.FALSE);
        } else {
            Operator not = operation.operator();
            appendBlank();
            appendOperator(not);
            appendPredicatePriority(operand, not);
        }
    }

    protected void appendBinaryOperation(OperatorNode operation) {
        ExpressionNode leftOperand = operation.firstOperand();
        Operator operator = operation.operator();
        ExpressionNode rightOperand = operation.secondOperand();
        appendBinaryOperation(leftOperand, operator, rightOperand);
    }

    protected void appendBinaryOperation(ExpressionNode leftOperand,
                                         Operator operator,
                                         ExpressionNode rightOperand) {
        ValueConverter<?, ?> convertor = getValueConverter(leftOperand);
        rightOperand = convertLiteralNode(convertor, rightOperand);
        appendExpressionPriority(leftOperand, operator);
        appendOperator(operator);
        appendExpressionPriority(rightOperand, operator);
    }

    protected void appendMultiOperation(OperatorNode operation) {
        appendFirstOperation(operation);
        appendRemainingOperands(operation, false);
    }

    protected void appendBetween(OperatorNode operation) {
        ExpressionNode leftOperand = operation.firstOperand();
        ValueConverter<?, ?> convertor = getValueConverter(leftOperand);
        Operator operator = operation.operator();
        appendBlank();
        appendExpression(leftOperand);
        appendOperator(operator);
        appendBlank();
        ExpressionNode second = Objects.requireNonNull(operation.secondOperand());
        second = convertLiteralNode(convertor, second);
        appendExpression(second);
        appendOperator(Operator.AND);
        appendBlank();
        ExpressionNode third = Objects.requireNonNull(operation.thirdOperand());
        third = convertLiteralNode(convertor, third);
        appendExpression(third);
    }

    protected void appendLogicalOperation(OperatorNode operation) {
        ExpressionNode leftOperand = operation.firstOperand();
        Operator operator = operation.operator();
        appendBlank();
        appendPredicatePriority(leftOperand, operator);
        appendRemainingOperands(operation, true);
    }

    private void appendRemainingOperands(OperatorNode operation, boolean predicate) {
        Operator operator = operation.operator();
        List<? extends ExpressionNode> operands = operation.operands();
        for (int i = 1; i < operands.size(); i++) {
            appendOperator(operator);
            appendWithPriority(operands.get(i), operator, predicate);
        }
    }

    protected void appendPredicatePriority(ExpressionNode expression, Operator operator) {
        appendWithPriority(expression, operator, true);
    }

    protected void appendExpressionPriority(ExpressionNode value, Operator operator) {
        appendWithPriority(value, operator, false);
    }

    private void appendWithPriority(ExpressionNode expression, Operator operator, boolean predicate) {
        Operator inner = getOperator(expression);
        if (inner != null && inner.priority() > operator.priority()) {
            sql.append('(');
            if (predicate) {
                appendPredicate(expression);
            } else {
                appendExpression(expression);
            }
            sql.append(')');
        } else {
            if (predicate) {
                appendPredicate(expression);
            } else {
                appendExpression(expression);
            }
        }
    }

    protected void appendOperator(Operator jdbcOperator) {
        String sign = jdbcOperator.sign();
        // 如果以字母开头，可能是函数名 - 使用方言映射
        if (Character.isLetter(sign.charAt(0))) {
            sign = dialect.functionName(sign);
            appendBlank();
        }
        sql.append(sign);
    }

    protected void appendPaths(PathNode column) {
        appendBlank();
        int iMax = column.deep() - 1;
        if (iMax == -1)
            throw new IllegalStateException();
        EntityAttribute attribute = getEntityType().getAttribute(column);
        appendAttribute(attribute);
    }

    /// 追加属性的 SQL 列引用。
    ///
    /// 对于根实体属性（deep == 1）和嵌入属性（{@code @Embedded}），使用主表别名；
    /// 对于关联属性，使用对应的 JOIN 表别名。
    ///
    /// @param attribute 实体属性
    protected void appendAttribute(EntityAttribute attribute) {
        if (attribute.deep() == 1 || attribute.declareBy().isEmbedded()) {
            appendFromAlias().append(".");
        } else {
            MetamodelSchema<?> parent = attribute.declareBy();
            if (!(parent instanceof JoinAttribute join)) {
                throw new IllegalStateException();
            }
            Integer index = joins.get(join);
            appendTableAlias(join, index).append('.');
        }
        String columnName;
        if (attribute instanceof EntityBasicAttribute eba) {
            columnName = eba.columnName();
        } else if (attribute instanceof JoinAttribute join) {
            columnName = join.getSourceAttribute().columnName();
        } else {
            throw new IllegalStateException(
                    "Unsupported entity attribute type '" + attribute.getClass().getName() +
                    "' for path '" + attribute.path() + "'.");
        }
        sql.append(leftQuotedIdentifier()).append(columnName).append(rightQuotedIdentifier());
    }

    protected void appendJoin() {
        for (Entry<JoinAttribute, Integer> entry : joins.entrySet()) {
            JoinAttribute k = entry.getKey();
            Integer v = entry.getValue();
            sql.append(LEFT_JOIN);
            appendTable(sql, k.getTargetEntityType());
            appendTableAlias(k, v);
            sql.append(ON);
            appendJoinCondition(sql, k, v);
        }
    }

    /// 追加单个 join 的连接条件
    ///
    /// 该方法用于构建 JOIN 的 ON 条件或 WHERE 中的连接条件，
    /// 被子类 {@code AbstractConditionalStatementBuilder} 复用于 PostgreSQL 方言处理。
    ///
    /// @param sb 目标 StringBuilder
    /// @param k join 属性元数据
    /// @param v join 表别名索引
    protected void appendJoinCondition(StringBuilder sb, JoinAttribute k, Integer v) {
        MetamodelSchema<?> declared = k.declareBy();
        if (declared instanceof JoinAttribute schemaAttribute) {
            Integer parentIndex = joins.get(schemaAttribute);
            appendTableAliasTo(sb, schemaAttribute, parentIndex);
        } else {
            sb.append(fromAlias);
        }
        if (k.isObject()) {
            EntityBasicAttribute source = k.getSourceAttribute();
            EntityBasicAttribute targeted = k.getTargetAttribute();
            sb.append(".").append(source.columnName()).append("=");
            appendTableAliasTo(sb, k, v);
            sb.append(".").append(targeted.columnName());
        } else {
            throw new IllegalStateException();
        }
    }

    protected void addJoin(SelectItem column) {
        switch (column) {
            case SelectItem.Joined joined -> addJoinAttribute(joined.join());
            case SelectItem.Expr(var source, var _) -> addJoin(source);
            case null -> throw new IllegalArgumentException("Column must not be null");
            default -> throw new IllegalArgumentException("Unsupported Column type: " + column.getClass().getName());
        }
    }

    protected void addJoin(ExpressionNode select) {
        if (select instanceof PathNode path) {
            EntityType entityType = getEntityType();
            MetamodelAttribute attribute = entityType.getAttribute(path);
            addJoin(attribute);
        } else if (select instanceof OperatorNode) {
            addJoin(((OperatorNode) select).operands());
        }
    }

    /// 收集属性路径上需要的 JOIN 关联。
    ///
    /// 从属性的声明链底部向上遍历，将非嵌入的关联属性收集到待 JOIN 列表中。
    /// 嵌入属性（{@code @Embedded}）共享同一张表，不需要 JOIN，直接跳过。
    ///
    /// @param attribute 要处理的属性
    protected void addJoin(MetamodelAttribute attribute) {
        ArrayDeque<JoinAttribute> joinAttributes = new ArrayDeque<>(attribute.deep());
        MetamodelSchema<?> join = attribute.declareBy();
        while (join instanceof JoinAttribute schemaAttribute) {
            if (!schemaAttribute.schema().isEmbedded()) {
                joinAttributes.addFirst(schemaAttribute);
            }
            join = schemaAttribute.declareBy();
        }
        for (JoinAttribute joinAttribute : joinAttributes) {
            addJoinAttribute(joinAttribute);
        }
    }

    private void addJoinAttribute(JoinAttribute joinAttribute) {
        JoinAttribute cur = joinAttribute;
        while (cur != null) {
            if (joins.putIfAbsent(cur, joins.size()) != null) {
                break;
            }
            cur = cur.declareBy() instanceof JoinAttribute join ? join : null;
        }
    }

    protected void addJoinPrimitive(Collection<? extends SelectItem> columns) {
        if (columns != null && !columns.isEmpty()) {
            for (SelectItem column : columns) {
                addJoin(column);
            }
        }
    }

    protected void addJoin(List<? extends ExpressionNode> expressions) {
        if (expressions != null && !expressions.isEmpty()) {
            for (ExpressionNode expression : expressions) {
                addJoin(expression);
            }
        }
    }

    protected void appendTable(StringBuilder append, EntitySchema entityTypeInfo) {
        if (entityTypeInfo instanceof SubQueryEntityType) {
            append.append('(').append(((SubQueryEntityType) entityTypeInfo).subSelectSql()).append(')');
        } else {
            append.append(leftQuotedIdentifier()).append(entityTypeInfo.tableName()).append(rightQuotedIdentifier());
        }
    }

    Operator getOperator(ExpressionNode expression) {
        return expression instanceof OperatorNode ? ((OperatorNode) expression).operator() : null;
    }

    protected StringBuilder appendTableAlias(JoinAttribute attribute, Integer index) {
        return appendTableAliasTo(this.sql, attribute, index);
    }

    protected StringBuilder appendTableAliasTo(StringBuilder sb, JoinAttribute attribute, Integer index) {
        String tableName = attribute.getTargetEntityType().type().getSimpleName();
        sb.append(shortAlias(tableName));
        if (subIndex > 0) {
            sb.append(subIndex).append("_");
        }
        return sb.append(index);
    }

}