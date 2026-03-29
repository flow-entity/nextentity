package io.github.nextentity.jdbc;

import io.github.nextentity.api.SortOrder;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import jakarta.persistence.LockModeType;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

///
/// @author HuangChengwei
/// @since 1.0.0
///
public abstract class AbstractQuerySqlBuilder {

    protected static final String NONE_DELIMITER = "";
    protected static final String DELIMITER = ",";
    protected static final String FOR_SHARE = " for share";
    protected static final String FOR_UPDATE = " for update";
    protected static final String FOR_UPDATE_NOWAIT = " for update nowait";
    protected static final String SELECT = "select ";
    protected static final String DISTINCT = "distinct ";
    protected static final String FROM = "from ";
    protected static final String WHERE = " where ";
    protected static final String HAVING = " having ";
    protected static final String ORDER_BY = " order by ";
    protected static final String DESC = "desc";
    protected static final String ASC = "asc";
    protected static final String LEFT_JOIN = " left join ";
    protected static final String ON = " on ";

    protected final StringBuilder sql;
    protected final List<Object> args;
    protected final Map<JoinAttribute, Integer> joins = new LinkedHashMap<>();

    protected final QueryContext context;

    protected final String fromAlias;
    protected final int subIndex;
    protected final AtomicInteger selectIndex;

    protected AbstractQuerySqlBuilder(StringBuilder sql,
                                      List<Object> args,
                                      QueryContext context,
                                      AtomicInteger selectIndex,
                                      int subIndex) {
        this.sql = sql;
        this.args = args;
        this.subIndex = subIndex;
        this.selectIndex = selectIndex;
        this.context = context;
        String prefix;
        From from = context.getStructure().from();
        if (from instanceof FromEntity fromEntity) {
            prefix = shortAlias(fromEntity.type().getSimpleName());
        } else {
            prefix = "t";
        }
        fromAlias = subIndex == 0 ? prefix + "_" : prefix + subIndex + "_";
    }

    public AbstractQuerySqlBuilder(QueryContext context) {
        this(new StringBuilder(), new ArrayList<>(), context, new AtomicInteger(), 0);
    }

    protected abstract String leftQuotedIdentifier();

    protected abstract String rightQuotedIdentifier();


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
        for (SelectItem expression : context.getSelectedExpression()) {
            sql.append(join);
            appendExpression(expression);
            appendSelectAlias(expression);
            join = DELIMITER;
        }
    }

    protected void appendSelectAlias(SelectItem expression) {
        //
    }

    protected void appendSelectAlias(ExpressionNode expression) {
        if (selectIndex.get() != 0 || !(expression instanceof PathNode pathNode) || pathNode.size() != 1) {
            int index = selectIndex.getAndIncrement();
            String alias = Integer.toString(index, Character.MAX_RADIX);
            sql.append(" as _").append(alias);
        }
    }

    protected void appendLockModeType(LockModeType lockModeType) {
        if (lockModeType == LockModeType.PESSIMISTIC_READ) {
            sql.append(FOR_SHARE);
        } else if (lockModeType == LockModeType.PESSIMISTIC_WRITE) {
            sql.append(FOR_UPDATE);
        } else if (lockModeType == LockModeType.PESSIMISTIC_FORCE_INCREMENT) {
            sql.append(FOR_UPDATE_NOWAIT);
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
    }

    protected void appendSubQuery(QueryStructure queryStructure) {
        sql.append('(');
        QueryContext ctx = context.newContext(queryStructure);
        appendQueryStructure(ctx);
        sql.append(')');
    }

    protected abstract void appendQueryStructure(QueryContext subContext);

    protected void appendFromTable() {
        appendTable(sql, context.getEntityType());
        sql.append(" ");
    }

    protected StringBuilder appendFromAlias() {
        return appendFromAlias(sql);
    }

    protected StringBuilder appendFromAlias(StringBuilder sql) {
        return sql.append(fromAlias);
    }

    @NonNull
    protected String shortAlias(String symbol) {
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
        ExpressionNode where = context.getStructure().where();
        if (ExpressionNodes.isNullOrTrue(where)) {
            return;
        }
        sql.append(WHERE);
        appendPredicate(where);
    }

    protected void appendPredicate(ExpressionNode node) {
        if (node instanceof PathNode || node instanceof LiteralNode) {
            node = node.operate(Operator.EQ, LiteralNode.TRUE);
        }
        appendExpression(node);
    }

    protected void appendHaving() {
        ExpressionNode having = context.getStructure().having();
        if (ExpressionNodes.isNullOrTrue(having)) {
            return;
        }
        sql.append(HAVING);
        appendPredicate(having);
    }

    protected void appendExpression(SelectItem selectItem) {
        if (selectItem instanceof EntityAttribute entityAttribute) {
            appendAttribute(entityAttribute);
        } else {
            appendExpression(selectItem.expression());
        }
    }

    protected void appendExpression(ExpressionNode expression) {
        if (expression instanceof LiteralNode literalNode) {
            appendLiteral(literalNode);
        } else if (expression instanceof PathNode) {
            appendPaths((PathNode) expression);
        } else if (expression instanceof OperatorNode) {
            appendOperation((OperatorNode) expression);
        } else if (expression instanceof QueryStructure) {
            appendSubQuery(((QueryStructure) expression));
        } else {
            throw new UnsupportedOperationException("unknown type " + expression.getClass());
        }
    }

    protected void appendLiteral(LiteralNode literal) {
        appendBlank().append('?');
        this.args.add(literal.value());
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

    private ValueConverter<?, ?> getValueConverter(ExpressionNode leftOperand) {
        ValueConverter<?, ?> convertor = null;
        if (leftOperand instanceof PathNode pathNode) {
            Attribute attribute = pathNode.getAttribute(context.getEntityType());
            if (attribute instanceof DatabaseColumnAttribute columnAttribute) {
                convertor = columnAttribute.valueConvertor();
            }
        }
        if (convertor == null) {
            convertor = new IdentityValueConverter(Object.class);
        }
        return convertor;
    }

    private ExpressionNode convertLiteralNode(ValueConverter<?, ?> convertor, ExpressionNode node) {
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
        ExpressionNode leftOperand = operation.firstOperand();
        Operator not = operation.operator();
        appendBlank();
        appendOperator(not);
        appendPredicatePriority(leftOperand, not);
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
        ExpressionNode seconded = operation.secondOperand();
        seconded = convertLiteralNode(convertor, seconded);
        appendExpression(seconded);
        appendOperator(Operator.AND);
        appendBlank();
        ExpressionNode thirded = operation.thirdOperand();
        thirded = convertLiteralNode(convertor, thirded);
        appendExpression(thirded);
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

    private void appendPredicatePriority(ExpressionNode expression, Operator operator) {
        Operator append = getOperator(expression);
        if (append != null && append.priority() > operator.priority()) {
            sql.append('(');
            appendPredicate(expression);
            sql.append(')');
        } else {
            appendPredicate(expression);
        }
    }

    private void appendExpressionPriority(ExpressionNode value, Operator operator) {
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
        EntityAttribute attribute = (EntityAttribute) column.getAttribute(context.getEntityType());
        appendAttribute(attribute);
    }

    private void appendAttribute(EntityAttribute attribute) {
        if (attribute.deep() == 1) {
            appendFromAlias().append(".");
        } else {
            JoinAttribute join = (JoinAttribute) attribute.declareBy();
            Integer index = joins.get(join);
            appendTableAlias(index).append('.');
        }
        sql.append(leftQuotedIdentifier()).append(attribute.columnName()).append(rightQuotedIdentifier());
    }

    protected void appendJoin() {
        for (Entry<JoinAttribute, Integer> entry : joins.entrySet()) {
            JoinAttribute k = entry.getKey();
            Integer v = entry.getValue();
            StringBuilder append = sql.append(LEFT_JOIN);
            appendTable(append, k);

            appendTableAlias(v);
            sql.append(ON);
            Schema declared = k.declareBy();
            if (declared instanceof JoinAttribute schemaAttribute) {
                Integer parentIndex = joins.get(schemaAttribute);
                appendTableAlias(parentIndex);
            } else {
                appendFromAlias(sql);
            }
            if (k.isObject()) {
                sql.append(".").append(k.joinName()).append("=");
                appendTableAlias(v);
                String referenced = k.referencedColumnName();
                if (referenced.isEmpty()) {
                    referenced = k.id().columnName();
                }
                sql.append(".").append(referenced);
            } else {
                throw new IllegalStateException();
            }
        }

    }

    private void initJoinColumnIndex() {
        QueryStructure structure = context.getStructure();
        addJoinPrimitive(context.getSelectedExpression());
        addJoin(structure.where());
        addJoin(structure.groupBy());
        for (SortExpression order : structure.orderBy()) {
            addJoin(order.expression());
        }
        addJoin(structure.having());
    }

    private void addJoin(SelectItem select) {
        if (select instanceof Attribute attribute) {
            addJoin(attribute);
        } else {
            addJoin(select.expression());
        }
    }

    private void addJoin(ExpressionNode select) {
        if (select instanceof PathNode) {
            EntityType entityType = context.getEntityType();
            Attribute attribute = entityType.getAttribute((PathNode) select);
            addJoin(attribute);
        } else if (select instanceof OperatorNode) {
            addJoin(((OperatorNode) select).operands());
        }
    }

    private void addJoin(Attribute attribute) {
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

    private void addJoinPrimitive(ImmutableArray<SelectItem> operands) {
        if (operands != null && !operands.isEmpty()) {
            for (SelectItem operand : operands) {
                addJoin(operand);
            }
        }
    }

    private void addJoin(List<? extends ExpressionNode> operands) {
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

    protected StringBuilder appendTableAlias(Integer index) {
        StringBuilder sql = this.sql;
        EntityType entityType = context.getEntityType();
        String tableName = entityType.type().getSimpleName();
        StringBuilder append = appendBlank(sql).append(shortAlias(tableName));
        if (subIndex > 0) {
            sql.append(subIndex).append("_");
        }
        return append.append(index).append("_");
    }

    protected abstract void appendOffsetAndLimit();

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
        ImmutableArray<SelectItem> primitives = context.getSelectedExpression();
        for (int i = 0; i < primitives.size(); i++) {
            SelectItem primitive = primitives.get(i);
            if (primitive.equals(order.expression())) {
                return i;
            }
        }
        return -1;
    }

}
