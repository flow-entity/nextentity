package io.github.nextentity.spring;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;
import org.springframework.boot.jpa.autoconfigure.JpaBaseConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


@Configuration
@AutoConfigureAfter({JdbcTemplateAutoConfiguration.class, JpaBaseConfiguration.class})
public class NextEntityAutoConfiguration {

    @Bean
    @ConditionalOnBean(JdbcTemplate.class)
    @ConditionalOnMissingBean(JdbcRepositoryFactoryConfiguration.class)
    protected JdbcRepositoryFactoryConfiguration jdbcRepositoryFactoryConfiguration() {
        return new JdbcRepositoryFactoryConfiguration();
    }


    @Bean
    @ConditionalOnBean({EntityManagerFactory.class, JdbcRepositoryFactoryConfiguration.class})
    @ConditionalOnMissingBean({JpaRepositoryFactoryConfiguration.class})
    protected JpaRepositoryFactoryConfiguration jpaRepositoryFactoryConfiguration(
            EntityManagerFactory entityManagerFactory,
            JdbcRepositoryFactoryConfiguration jdbcRepositoryFactoryConfiguration) {
        return new JpaRepositoryFactoryConfiguration(entityManagerFactory, jdbcRepositoryFactoryConfiguration);
    }

}
