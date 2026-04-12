package io.github.nextentity.examples.integration;

import io.github.nextentity.core.EntityTemplateFactory;
import io.github.nextentity.examples.entity.Customer;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.spring.Repository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestConfig.class)
public class RepositoryBeanRegistrarTest {

    @Autowired(required = false)
    EntityManager entityManager;
    @Autowired(required = false)
    EntityTemplateFactory entityTemplateFactory;
    @Autowired(required = false)
    private Repository<Customer, Long> customerRepository;
    @Autowired(required = false)
    private Repository<Employee, Long> employeeLongRepository;

    @Test
    void exists() {
        assertThat(entityTemplateFactory).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(customerRepository).isNotNull();
        assertThat(employeeLongRepository).isNotNull();
    }
}