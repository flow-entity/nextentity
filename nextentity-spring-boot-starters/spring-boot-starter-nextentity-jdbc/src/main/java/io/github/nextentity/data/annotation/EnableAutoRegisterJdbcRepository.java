package io.github.nextentity.data.annotation;

import io.github.nextentity.data.jdbc.JdbcRepositoryConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.*;
import java.util.Map;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableAutoRegisterJdbcRepository.AutoRegisterRepositoryConfigurationSelector.class)
public @interface EnableAutoRegisterJdbcRepository {
    class AutoRegisterRepositoryConfigurationSelector implements ImportSelector {
        @Override
        public String @NotNull [] selectImports(AnnotationMetadata metadata) {
            Map<String, Object> attributes = metadata.getAnnotationAttributes(
                    EnableAutoRegisterJdbcRepository.class.getName());
            boolean enabled = attributes != null;

            if (enabled) {
                return new String[]{JdbcRepositoryConfiguration.class.getName()};
            }
            return new String[0];
        }
    }
}
