package io.github.nextentity.core.constructor;

import io.github.nextentity.jdbc.Arguments;

import java.util.List;

/// 懒加载值构造器
///
/// 负责创建 AttributeLoader，首次访问时触发批量加载。
/// 内部持有外键列和批量加载器，不需要 ArrayConstructor 包装外键。
///
/// @author HuangChengwei
/// @since 2.2.2
public class LazyValueConstructor implements ValueConstructor {

    private final Column foreignKeyColumn;
    private final LazyLoadContext lazyLoadContext;

    public LazyValueConstructor(Column foreignKeyColumn, LazyLoadContext lazyLoadContext) {
        this.foreignKeyColumn = foreignKeyColumn;
        this.lazyLoadContext = lazyLoadContext;
    }

    @Override
    public List<Column> columns() {
        return List.of(foreignKeyColumn);
    }

    @Override
    public Object construct(Arguments arguments) {
        Object foreignKey = foreignKeyColumn.getValue(arguments);
        return lazyLoadContext.getAttributeLoader(foreignKey);
    }
}