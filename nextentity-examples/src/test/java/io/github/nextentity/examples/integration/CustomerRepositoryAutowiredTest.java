package io.github.nextentity.examples.integration;

import io.github.nextentity.examples.entity.Customer;
import io.github.nextentity.spring.Repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestConfig.class)
public class CustomerRepositoryAutowiredTest {
    @Autowired(required = false)
    private Repository<Customer, Long> customerRepository;

    @Test
    void exists() {
        assertThat(customerRepository).isNotNull();
    }
}