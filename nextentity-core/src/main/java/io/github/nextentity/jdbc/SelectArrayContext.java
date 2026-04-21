package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryConfig;
import io.github.nextentity.core.QueryDescriptor;
import io.github.nextentity.core.Tuples;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.expression.SelectExpressions;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

public class SelectArrayContext extends QueryContext {

    private SelectExpressions selectExpressions;

    private ImmutableArray<io.github.nextentity.core.SelectItem> expressions;
    private ImmutableArray<Object> selectExpressionItems;

    public SelectArrayContext(QueryConfig descriptor) {
        super(descriptor);
    }

    /// 设置多表达式选择定义
    public void setSelectExpressions(SelectExpressions selectExpressions) {
        this.selectExpressions = selectExpressions;
    }

    /// 初始化（无参版本）
    @Override
    public void init() {
        super.init();
        this.selectExpressionItems = selectExpressions.items().stream()
                .map(it -> it instanceof PathNode pathExpression
                        ? getEntityType().getAttribute(pathExpression) : it)
                .collect(ImmutableList.collector(selectExpressions.items().size()));
        this.expressions = selectExpressions.items().stream()
                .flatMap(e -> stream(getEntityType(), e, DeepLimitSchemaAttributePaths.of(0)))
                .collect(ImmutableList.collector());
    }

    @Override
    public ImmutableArray<io.github.nextentity.core.SelectItem> getSelectedExpression() {
        return expressions;
    }

    @Override
    public Object doConstruct(Arguments arguments) {
        Object[] objects = new Object[selectExpressionItems.size()];
        for (int i = 0; i < objects.length; i++) {
            Object expression = selectExpressionItems.get(i);
            objects[i] = constructExpression(getEntityType(), arguments, expression);
        }
        return Tuples.of(objects);
    }

}
