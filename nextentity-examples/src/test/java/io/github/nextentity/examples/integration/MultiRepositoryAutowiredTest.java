package io.github.nextentity.examples.integration;

import io.github.nextentity.examples.entity.Customer;
import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.spring.EntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试多个不同泛型的 Repository 注入。
@SpringBootTest
@Import(TestConfig.class)
public class MultiRepositoryAutowiredTest {

    @Autowired(required = false)
    private EntityRepository<Customer, Long> customerRepository;

    @Autowired(required = false)
    private EntityRepository<Department, Long> departmentRepository;

    @Test
    void bothRepositoriesExist() {
        assertThat(customerRepository).isNotNull();
        assertThat(departmentRepository).isNotNull();
    }

}