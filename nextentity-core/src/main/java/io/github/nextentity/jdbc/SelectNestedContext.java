package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryConfig;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.expression.SelectNested;
import io.github.nextentity.core.expression.Selected;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/// 处理嵌套选择的 QueryContext 实现。
///
/// SelectNested 包含多个 Selected 子项，每个子项可以是任意 Selected 类型。
/// 此 Context 递归处理每个子选择，组合所有表达式和构建结果。
///
/// @author HuangChengwei
/// @since 1.0.0
public class SelectNestedContext extends QueryContext {

    private SelectNested selectNested;

    private ImmutableList<QueryContext> subContexts;
    private ImmutableArray<SelectItem> expressions;

    public SelectNestedContext(QueryConfig descriptor) {
        super(descriptor);
    }

    /// 设置嵌套选择定义
    public void setSelectNested(SelectNested selectNested) {
        this.selectNested = selectNested;
    }

    /// 初始化（无参版本）
    @Override
    public void init() {
        super.init();

        // 为每个子选择创建对应的 Context
        List<QueryContext> contextList = new ArrayList<>(selectNested.items().size());
        List<SelectItem> expressionList = new ArrayList<>();

        for (Selected item : selectNested.items()) {
            QueryContext subContext = createSubContext(item);
            contextList.add(subContext);
            // 收集子 Context 的表达式
            subContext.getSelectedExpression().forEach(expressionList::add);
        }

        this.subContexts = ImmutableList.ofCollection(contextList);
        this.expressions = ImmutableList.ofCollection(expressionList);
    }

    /// 创建子选择的 Context
    private QueryContext createSubContext(Selected selected) {
        QueryContext context = create(descriptor, structure.select(selected));
        context.setExpandReferencePath(expandReferencePath);
        context.init();
        return context;
    }

    @Override
    public ImmutableArray<SelectItem> getSelectedExpression() {
        return expressions;
    }

    @Override
    public Object doConstruct(Arguments arguments) {
        // 递归调用每个子 Context 的 construct 方法
        Object[] results = new Object[subContexts.size()];
        for (int i = 0; i < subContexts.size(); i++) {
            QueryContext subContext = subContexts.get(i);
            results[i] = subContext.doConstruct(arguments);
        }
        return results;
    }
}