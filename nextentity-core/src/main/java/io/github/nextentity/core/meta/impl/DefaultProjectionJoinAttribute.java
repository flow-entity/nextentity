package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import jakarta.persistence.FetchType;

import java.util.ArrayList;

public class DefaultProjectionJoinAttribute
        extends DefaultProjectionSchema
        implements ProjectionJoinAttribute {

    private final DefaultProjectionSchema declareBy;
    private final EntityBasicAttribute sourceAttribute;
    private final EntityBasicAttribute targetAttribute;
    private final EntityType target;
    private final FetchType fetchType;

    private final Accessor accessor;
    private final PathNode path;

    public DefaultProjectionJoinAttribute(DefaultProjectionSchema declareBy,
                                          EntityBasicAttribute sourceAttribute,
                                          EntityBasicAttribute targetAttribute,
                                          EntityType target,
                                          DefaultMetamodel metamodel,
                                          MetamodelAttribute attribute) {
        super(declareBy.getEntitySchema(), attribute.type(), metamodel);
        this.declareBy = declareBy;
        this.sourceAttribute = sourceAttribute;
        this.targetAttribute = targetAttribute;
        this.target = target;
        this.accessor = attribute.accessor();
        this.path = declareBy.getPath(attribute.name());
        this.fetchType = metamodel.getResolver().getFetchType(this);
    }

    @Override
    protected AttributeSet<ProjectionAttribute> createAttributes() {
        ProjectionSchema projection = target.getProjection(type());
        ArrayList<ProjectionAttribute> result = new ArrayList<>();
        for (ProjectionAttribute projectionAttribute : projection.getAttributes()) {
            var item = ProjectionAttributeFactory.createAttribute(
                    this,
                    projectionAttribute,
                    metamodel
            );
            result.add(item);
        }
        return new AttributeSet<>(result);
    }

    @Override
    public EntityAttribute getEntityAttribute() {
        return sourceAttribute;
    }

    /// 获取此自定义连接属性的目标投影 Schema（自身即为投影 Schema）。
    ///
    /// @return 当前实例
    @Override
    public ProjectionSchema schema() {
        return this;
    }

    @Override
    public EntityType getTargetEntityType() {
        return target;
    }

    @Override
    public EntityBasicAttribute getSourceAttribute() {
        return sourceAttribute;
    }

    @Override
    public EntityBasicAttribute getTargetAttribute() {
        return targetAttribute;
    }

    @Override
    public Accessor accessor() {
            return accessor;
    }

    @Override
    public ProjectionSchema declareBy() {
        return declareBy;
    }

    @Override
    public PathNode path() {
        return path;
    }

    @Override
    public FetchType getFetchType() {
        return fetchType;
    }
}
