package io.github.nextentity.spring.integration.projection;

import io.github.nextentity.core.meta.ProjectionType;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import io.github.nextentity.spring.integration.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// IUser 测试类
///
/// @author HuangChengwei
class IUserTest {

    private static final Logger log = LoggerFactory.getLogger(IUserTest.class);

    public static void main(String[] args) {
        ProjectionType projection = JpaMetamodel.of().getEntity(User.class).getProjection(IUser.class);
        ProjectionType parentUser = (ProjectionType) projection.getAttribute("parentUser");
        log.info("{}", parentUser.attributes());
    }

}