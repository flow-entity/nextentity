package io.github.nextentity.data.jpa;

import io.github.nextentity.api.Repository;
import io.github.nextentity.core.RepositoryFactory;
import io.github.nextentity.data.EntityTypeUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import java.io.Serializable;

import static io.github.nextentity.data.jpa.NextEntityJpaAutoConfiguration.JPA_REPOSITORY_FACTORY_BEAN_NAME;

@Configuration
@Import(NextEntityJpaAutoConfiguration.class)
public class JpaRepositoryConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    protected <T, ID extends Serializable>
    Repository<ID, T> jpaRepository(DependencyDescriptor descriptor,
                                    @Qualifier(JPA_REPOSITORY_FACTORY_BEAN_NAME)
                                    RepositoryFactory factory) {
        Class<T> entityType = EntityTypeUtil.getEntityType(descriptor);
        EntityTypeUtil.checkIdType(descriptor, factory.getMetamodel(), entityType);
        return factory.getRepository(entityType);
    }

}
