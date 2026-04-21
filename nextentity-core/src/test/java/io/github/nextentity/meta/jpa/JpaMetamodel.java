package io.github.nextentity.meta.jpa;

import io.github.nextentity.core.meta.MetamodelResolver;
import io.github.nextentity.core.meta.impl.DefaultMetamodel;
import io.github.nextentity.core.meta.impl.DefaultMetamodelResolver;

@Deprecated
public class JpaMetamodel extends DefaultMetamodel {
    public JpaMetamodel(MetamodelResolver resolver) {
        super(resolver);
    }

    public static JpaMetamodel of() {
        return new JpaMetamodel(DefaultMetamodelResolver.of());
    }
}
