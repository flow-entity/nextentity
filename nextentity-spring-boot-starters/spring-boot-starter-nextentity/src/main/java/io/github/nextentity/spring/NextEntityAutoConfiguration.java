package io.github.nextentity.spring;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;


@Configuration
@AutoConfigureAfter({JdbcTemplateAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
public class NextEntityAutoConfiguration {

    @Bean
    @ConditionalOnBean(JdbcTemplate.class)
    @ConditionalOnMissingBean(JdbcRepositoryFactoryConfiguration.class)
    protected JdbcRepositoryFactoryConfiguration jdbcRepositoryFactoryConfiguration(JdbcTemplate jdbcTemplate) throws SQLException {
        return new JdbcRepositoryFactoryConfiguration(jdbcTemplate);
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
