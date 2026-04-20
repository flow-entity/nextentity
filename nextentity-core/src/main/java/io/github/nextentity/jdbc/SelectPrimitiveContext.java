package io.github.nextentity.jdbc;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.SelectExpression;
import io.github.nextentity.core.util.ImmutableArray;

public class SelectPrimitiveContext extends QueryContext {

    private SelectExpression selectExpression;

    private ImmutableArray<SelectItem> expressions;
    private ExpressionNode expression;

    /// 无参构造函数
    public SelectPrimitiveContext() {
    }

    /// 设置表达式选择定义
    public void setSelectExpression(SelectExpression selectExpression) {
        this.selectExpression = selectExpression;
    }

    /// 初始化（无参版本）
    @Override
    public void init() {
        super.init();
        this.expression = selectExpression.expression();
        this.expressions = getSelectPrimitiveExpressions(entityType, expression, DeepLimitSchemaAttributePaths.of(0));
    }

    @Override
    public ImmutableArray<SelectItem> getSelectedExpression() {
        return expressions;
    }

    @Override
    public Object doConstruct(Arguments arguments) {
        return constructExpression(entityType, arguments, expression);
    }
}
