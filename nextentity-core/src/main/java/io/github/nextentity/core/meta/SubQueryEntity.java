package io.github.nextentity.core.meta;

import java.util.function.BiFunction;

public class SubQueryEntity extends SimpleEntity implements SubQueryEntityType {

    private final String subSelectSql;

    public SubQueryEntity(Class<?> type,
                          String tableName,
                          BiFunction<EntityType, Class<?>, ProjectionType> projectionTypeGenerator,
                          String subSelectSql) {
        super(type, tableName, projectionTypeGenerator);
        this.subSelectSql = subSelectSql;
    }

    @Override
    public String subSelectSql() {
        return subSelectSql;
    }
}
