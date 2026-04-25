package io.github.nextentity.jdbc;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.constructor.Column;
import io.github.nextentity.core.constructor.QueryContext;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.meta.impl.IdentityValueConverter;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
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
        return symbol.toLowerCase().substring(0, 1);
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

    protected void appendExpression(Column column) {
        ExpressionNode source = column.source();
        if (column instanceof Column.JoinedAttr attr) {
            appendAttribute(attr.attribute());
        } else if (source instanceof PathNode pathNode) {
            appendAttribute(getEntityType().getAttribute(pathNode));
        } else {
            appendExpression(source);
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
        if (operation.operands().size() <= 1) {
            appendBlank().append(0);
        } else {
            appendBlank();
            appendExpression(leftOperand);
            appendOperator(operator);
            List<? extends ExpressionNode> operands = operation.operands();
            boolean notSingleSubQuery = operands.size() != 2 || !(operands.get(1) instanceof QueryStructure);
            char join = notSingleSubQuery ? '(' : ' ';
            for (int i = 1; i < operands.size(); i++) {
                ExpressionNode expression = operands.get(i);
                expression = convertLiteralNode(convertor, expression);
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
        ValueConverter<?, ?> convertor = null;
        if (leftOperand instanceof PathNode pathNode) {
            Attribute attribute = getEntityType().getAttribute(pathNode);
            if (attribute instanceof EntityBasicAttribute columnAttribute) {
                convertor = columnAttribute.valueConvertor();
            }
        }
        if (convertor == null) {
            convertor = new IdentityValueConverter<>(Object.class);
        }
        return convertor;
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
            ExpressionNode leftOperand = operation.firstOperand();
            Operator not = operation.operator();
            appendBlank();
            appendOperator(not);
            appendPredicatePriority(leftOperand, not);
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
        Operator operator = operation.operator();
        List<? extends ExpressionNode> operands = operation.operands();
        for (int i = 1; i < operands.size(); i++) {
            ExpressionNode value = operands.get(i);
            appendOperator(operator);
            appendExpressionPriority(value, operator);
        }
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
        List<? extends ExpressionNode> operands = operation.operands();
        for (int i = 1; i < operands.size(); i++) {
            ExpressionNode value = operands.get(i);
            appendOperator(operator);
            appendPredicatePriority(value, operator);
        }
    }

    protected void appendPredicatePriority(ExpressionNode expression, Operator operator) {
        Operator append = getOperator(expression);
        if (append != null && append.priority() > operator.priority()) {
            sql.append('(');
            appendPredicate(expression);
            sql.append(')');
        } else {
            appendPredicate(expression);
        }
    }

    protected void appendExpressionPriority(ExpressionNode value, Operator operator) {
        Operator next = getOperator(value);
        if (next != null && next.priority() > operator.priority()) {
            sql.append('(');
            appendExpression(value);
            sql.append(')');
        } else {
            appendExpression(value);
        }
    }

    protected void appendOperator(Operator jdbcOperator) {
        String sign = jdbcOperator.sign();
        // 如果以字母开头，可能是函数名 - 使用方言映射
        if (Character.isLetter(sign.charAt(0))) {
            sign = dialect.functionName(sign);
        }
        appendOperator(sign);
    }

    protected void appendOperator(String sign) {
        if (Character.isLetter(sign.charAt(0))) {
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

    protected void appendAttribute(EntityAttribute attribute) {
        if (attribute.deep() == 1) {
            appendFromAlias().append(".");
        } else {
            JoinAttribute join = (JoinAttribute) attribute.declareBy();
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
            appendJoinCondition(k, v);
        }
    }

    /// 追加单个 join 的连接条件
    ///
    /// 该方法用于构建 JOIN 的 ON 条件或 WHERE 中的连接条件，
    /// 被子类 {@code AbstractConditionalStatementBuilder} 复用于 PostgreSQL 方言处理。
    ///
    /// @param k join 属性元数据
    /// @param v join 表别名索引
    protected void appendJoinCondition(JoinAttribute k, Integer v) {
        Schema declared = k.declareBy();
        if (declared instanceof JoinAttribute schemaAttribute) {
            Integer parentIndex = joins.get(schemaAttribute);
            appendTableAlias(schemaAttribute, parentIndex);
        } else {
            appendFromAlias(sql);
        }
        if (k.isObject()) {
            EntityBasicAttribute source = k.getSourceAttribute();
            EntityBasicAttribute targeted = k.getTargetAttribute();
            sql.append(".").append(source.columnName()).append("=");
            appendTableAlias(k, v);
            String referenced = targeted.columnName();
            sql.append(".").append(referenced);
        } else {
            throw new IllegalStateException();
        }
    }

    protected void addJoin(Column column) {
        if (column instanceof Column.JoinedAttr attr) {
            addJoin(attr.attribute());
        } else {
            addJoin(column.source());
        }
    }

    protected void addJoin(ExpressionNode select) {
        if (select instanceof PathNode) {
            EntityType entityType = getEntityType();
            Attribute attribute = entityType.getAttribute((PathNode) select);
            addJoin(attribute);
        } else if (select instanceof OperatorNode) {
            addJoin(((OperatorNode) select).operands());
        }
    }

    protected void addJoin(Attribute attribute) {
        ArrayDeque<JoinAttribute> joinAttributes = new ArrayDeque<>(attribute.deep());
        Schema join = attribute.declareBy();
        while (join instanceof JoinAttribute schemaAttribute) {
            joinAttributes.addFirst(schemaAttribute);
            join = schemaAttribute.declareBy();
        }
        for (JoinAttribute joinAttribute : joinAttributes) {
            joins.putIfAbsent(joinAttribute, joins.size());
        }
    }

    protected void addJoinPrimitive(Collection<? extends Column> operands) {
        if (operands != null && !operands.isEmpty()) {
            for (Column operand : operands) {
                addJoin(operand);
            }
        }
    }

    protected void addJoin(List<? extends ExpressionNode> operands) {
        if (operands != null && !operands.isEmpty()) {
            for (ExpressionNode operand : operands) {
                addJoin(operand);
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