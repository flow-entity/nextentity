package io.github.nextentity.jpa;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.*;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.From;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

///
/// JPA 表达式构建器，用于将 NextEntity 表达式节点转换为 JPA Criteria API 表达式。
/// 该类提供了将各种表达式节点（字面量、路径、操作符等）转换为对应的 JPA 表达式的功能。
///
/// @author HuangChengwei
/// @since 2.0.0
@SuppressWarnings("PatternVariableCanBeUsed")
public class JpaExpressionBuilder {

    protected final Root<?> root;

    protected final CriteriaBuilder cb;

    protected final Map<PathNode, FetchParent<?, ?>> fetched = new HashMap<>();

    public JpaExpressionBuilder(Root<?> root, CriteriaBuilder cb) {
        this.root = root;
        this.cb = cb;
    }

    public Expression<?> toExpression(SelectItem selectItem) {
        return toExpression(selectItem.expression());
    }

    public Expression<?> toExpression(ExpressionNode expression) {
        if (expression instanceof LiteralNode) {
            LiteralNode literal = (LiteralNode) expression;
            return cb.literal(literal.value());
        }
        if (expression instanceof PathNode) {
            PathNode path = (PathNode) expression;
            return getPath(path);
        }
        if (expression instanceof OperatorNode) {
            OperatorNode operation = (OperatorNode) expression;
            Operator operator = operation.operator();
            ExpressionNode e1 = operation.secondOperand();
            ExpressionNode e2 = operation.thirdOperand();
            switch (operator) {
                case NOT:
                    return toPredicate(firstExpression(operation)).not();
                case AND: {
                    Predicate[] predicates = toPredicateArray(operation.operands());
                    return cb.and(predicates);
                }
                case OR: {
                    Predicate[] predicates = toPredicateArray(operation.operands());
                    return cb.or(predicates);
                }
                case EQ: {
                    return cb.equal(firstExpression(operation), toExpression(e1));
                }
                case NE: {
                    return cb.notEqual(firstExpression(operation), toExpression(e1));
                }
                case GT: {
                    return cb.gt(firstExpression(operation), cast(toExpression(e1)));
                }
                case GE: {
                    return cb.ge(firstExpression(operation), cast(toExpression(e1)));
                }
                case LT: {
                    return cb.lt(firstExpression(operation), cast(toExpression(e1)));
                }
                case LE: {
                    return cb.le(firstExpression(operation), cast(toExpression(e1)));
                }
                case LIKE: {
                    return cb.like(firstExpression(operation), cast(toExpression(e1)));
                }
                case IS_NULL:
                    return cb.isNull(firstExpression(operation));
                case IS_NOT_NULL:
                    return cb.isNotNull(firstExpression(operation));
                case IN: {
                    List<? extends ExpressionNode> operands = operation.operands();
                    if (operands.size() <= 1) {
                        return cb.literal(false);
                    } else {
                        CriteriaBuilder.In<Object> in = cb.in(firstExpression(operation));
                        for (int i = 1; i < operands.size(); i++) {
                            ExpressionNode arg = operands.get(i);
                            in = in.value(toExpression(arg));
                        }
                        return in;
                    }
                }
                case BETWEEN: {
                    return cb.between(firstExpression(operation), cast(toExpression(e1)), cast(toExpression(e2)));
                }
                case LOWER:
                    return cb.lower(firstExpression(operation));
                case UPPER:
                    return cb.upper(firstExpression(operation));
                case SUBSTRING: {
                    List<? extends ExpressionNode> operands = operation.operands();
                    Expression<?> operand0 = firstExpression(operation);
                    if (operands.size() == 2) {
                        return cb.substring(cast(operand0), cast(toExpression(e1)));
                    } else if (operands.size() == 3) {
                        return cb.substring(cast(operand0), cast(toExpression(e1)), cast(toExpression(e2)));
                    } else {
                        throw new IllegalArgumentException("argument length error");
                    }
                }
                case TRIM:
                    return cb.trim(firstExpression(operation));
                case LENGTH:
                    return cb.length(firstExpression(operation));
                case ADD: {
                    return cb.sum(firstExpression(operation), cast(toExpression(e1)));
                }
                case SUBTRACT: {
                    return cb.diff(firstExpression(operation), cast(toExpression(e1)));
                }
                case MULTIPLY: {
                    return cb.prod(firstExpression(operation), cast(toExpression(e1)));
                }
                case DIVIDE: {
                    return cb.quot(firstExpression(operation), cast(toExpression(e1)));
                }
                case MOD: {
                    return cb.mod(firstExpression(operation), cast(toExpression(e1)));
                }
                case NULLIF: {
                    Expression<?> e0 = firstExpression(operation);
                    return cb.nullif(e0, toExpression(e1));
                }
                case IF_NULL: {
                    return cb.coalesce(firstExpression(operation), toExpression(e1));
                }
                case MIN:
                    return cb.min(firstExpression(operation));
                case MAX:
                    return cb.max(firstExpression(operation));
                case COUNT:
                    ExpressionNode operand = operation.firstOperand();
                    if (operand instanceof OperatorNode opt && opt.operator() == Operator.DISTINCT) {
                        return cb.countDistinct(toExpression(opt.firstOperand()));
                    } else {
                        return cb.count(firstExpression(operation));
                    }
                case AVG:
                    return cb.avg(firstExpression(operation));
                case SUM:
                    return cb.sum(firstExpression(operation));
                default:
                    throw new UnsupportedOperationException(operator.name());
            }
        } else {
            throw new UnsupportedOperationException("unknown expression type " + expression.getClass());
        }
    }

    private <X> Expression<X> firstExpression(OperatorNode ov) {
        return cast(toExpression(ov.firstOperand()));
    }

    public Predicate toPredicate(ExpressionNode expression) {
        return toPredicate(toExpression(expression));
    }

    private Predicate toPredicate(Expression<?> result) {
        if (result instanceof Predicate) {
            return (Predicate) result;
        }
        return cb.isTrue(cast(result));
    }

    @NonNull
    private Predicate[] toPredicateArray(List<? extends ExpressionNode> operands) {
        return operands.stream()
                .map(this::toPredicate)
                .toArray(Predicate[]::new);
    }

    public static <T> Expression<T> cast(Expression<?> expression) {
        return unsafeCast(expression);
    }

    public static <T> T unsafeCast(Object o) {
        return TypeCastUtil.unsafeCast(o);
    }

    protected Path<?> getPath(PathNode column) {
        From<?, ?> r = root;
        int size = column.deep();
        for (int i = 0; i < size; i++) {
            String s = column.get(i);
            if (i != size - 1) {
                PathNode offset = column.subLength(i + 1);
                r = join(offset);
            } else {
                return r.get(s);
            }
        }

        return r;
    }

    private Join<?, ?> join(PathNode column) {
        return (Join<?, ?>) fetched.compute(column, (k, v) -> {
            if (v instanceof Join<?, ?>) {
                return v;
            } else {
                From<?, ?> r = root;
                for (String s : column) {
                    r = r.join(s, JoinType.LEFT);
                }
                return r;
            }
        });
    }
}