package io.github.nextentity.data.jpa.annotation;

import io.github.nextentity.data.jpa.JpaRepositoryConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.*;
import java.util.Map;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableAutoRegisterJpaRepository.AutoRegisterRepositoryConfigurationSelector.class)
public @interface EnableAutoRegisterJpaRepository {
    class AutoRegisterRepositoryConfigurationSelector implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata metadata) {
            Map<String, Object> attributes = metadata.getAnnotationAttributes(
                    EnableAutoRegisterJpaRepository.class.getName());
            boolean enabled = attributes != null;

            if (enabled) {
                return new String[]{JpaRepositoryConfiguration.class.getName()};
            }
            return new String[0];
        }
    }
}
