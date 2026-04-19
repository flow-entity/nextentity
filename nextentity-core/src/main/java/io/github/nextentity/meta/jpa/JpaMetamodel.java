package io.github.nextentity.meta.jpa;

import io.github.nextentity.core.meta.MetamodelResolver;
import io.github.nextentity.core.meta.impl.DefaultMetamodel;
import io.github.nextentity.core.meta.impl.DefaultMetamodelResolver;

/// JPA 元模型实现，用于从 JPA 注解中提取实体元数据信息。
/// 该类提供了从 JPA 实体类中获取表名、字段名、关系映射等信息的功能。
///
/// @author HuangChengwei
/// @since 2.0.0
@Deprecated
public class JpaMetamodel extends DefaultMetamodel {
    public JpaMetamodel(MetamodelResolver resolver) {
        super(resolver);
    }

    public static JpaMetamodel of() {
        return new JpaMetamodel(DefaultMetamodelResolver.of());
    }
}
