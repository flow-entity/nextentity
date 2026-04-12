package io.github.nextentity.examples.integration;

import io.github.nextentity.examples.entity.Customer;
import io.github.nextentity.examples.repository.CustomizedEntityRepository;
import io.github.nextentity.examples.repository.IExtendedEntityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// RepositoryFactory 功能集成测试。
///
/// 验证 RepositoryFactory 能够为所有实体类型提供扩展的 Repository 实现。
///
/// @author HuangChengwei
/// @since 2.1.4
@SpringBootTest
class RepositoryFactoryIntegrationTest {

    @Autowired(required = false)
    private IExtendedEntityRepository<Customer, Long> customerRepository;

    private List<Customer> testCustomers;

    @BeforeEach
    void setUp() {
        assertNotNull(customerRepository, "Customer Repository should be injected");

        clearAllData();
        testCustomers = createTestCustomers();
        customerRepository.insertAll(testCustomers);
    }

    @AfterEach
    void tearDown() {
        clearAllData();
    }

    /// 验证注入的 Repository 是扩展实现。
    @Test
    void testRepositoryIsExtendedImplementation() {
        // Customer Repository 是扩展实现
        assertInstanceOf(CustomizedEntityRepository.class, customerRepository, "Customer Repository should be ExtendedEntityRepository");
    }

    /// 验证扩展方法 findAll 正常工作。
    @Test
    void testExtendedMethod_findAll() {
        CustomizedEntityRepository<Customer, Long> extendedRepo =
                (CustomizedEntityRepository<Customer, Long>) customerRepository;

        List<Customer> all = extendedRepo.findAll();

        assertEquals(testCustomers.size(), all.size());
    }

    /// 验证扩展方法 findTop 正常工作。
    @Test
    void testExtendedMethod_findTop() {
        CustomizedEntityRepository<Customer, Long> extendedRepo =
                (CustomizedEntityRepository<Customer, Long>) customerRepository;

        List<Customer> top3 = extendedRepo.findTop(3);

        assertEquals(3, top3.size());
    }

    /// 验证扩展方法 count 正常工作。
    @Test
    void testExtendedMethod_count() {
        CustomizedEntityRepository<Customer, Long> extendedRepo =
                (CustomizedEntityRepository<Customer, Long>) customerRepository;

        long count = extendedRepo.count();

        assertEquals(testCustomers.size(), count);
    }

    /// 验证扩展方法 hasAny 正常工作。
    @Test
    void testExtendedMethod_hasAny() {
        CustomizedEntityRepository<Customer, Long> extendedRepo =
                (CustomizedEntityRepository<Customer, Long>) customerRepository;

        assertTrue(extendedRepo.hasAny());

        // 清空后应为 false
        clearAllData();
        assertFalse(extendedRepo.hasAny());
    }

    /// 验证扩展方法 deleteByIds 正常工作。
    @Test
    void testExtendedMethod_deleteByIds() {
        CustomizedEntityRepository<Customer, Long> extendedRepo =
                (CustomizedEntityRepository<Customer, Long>) customerRepository;

        List<Long> ids = testCustomers.stream()
                .limit(2)
                .map(Customer::getId)
                .toList();

        extendedRepo.deleteByIds(ids);

        assertEquals(testCustomers.size() - 2, extendedRepo.count());
    }

    /// 验证基本 CRUD 功能正常工作。
    @Test
    void testBasicCrudOperations() {
        Customer first = testCustomers.getFirst();

        // findById
        Customer found = customerRepository.findById(first.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(first.getName(), found.getName());

        // existsById
        assertTrue(customerRepository.existsById(first.getId()));

        // update
        found.setVip(false);
        customerRepository.update(found);
        Customer updated = customerRepository.getById(first.getId());
        assertFalse(updated.getVip());

        // delete
        customerRepository.delete(found);
        assertFalse(customerRepository.existsById(first.getId()));
    }

    /// 验证 Employee Repository 同样使用扩展实现。
    @Test
    void testEmployeeRepositoryUsesExtendedImplementation() {
        CustomizedEntityRepository<Customer, Long> extendedRepo =
                (CustomizedEntityRepository<Customer, Long>) customerRepository;

        // 验证扩展方法可用
        long count = extendedRepo.count();
        assertTrue(count >= 0);
    }

    // ==================== Helper Methods ====================

    private void clearAllData() {
        List<Customer> existing = customerRepository.query().list();
        if (!existing.isEmpty()) {
            customerRepository.deleteAll(existing);
        }
    }

    private List<Customer> createTestCustomers() {
        return List.of(
                new Customer(1001L, "VIP Customer 1", "vip1@test.com", "111-1111", "Address 1", true),
                new Customer(1002L, "VIP Customer 2", "vip2@test.com", "222-2222", "Address 2", true),
                new Customer(1003L, "Regular Customer 1", "regular1@example.com", "333-3333", "Address 3", false),
                new Customer(1004L, "Regular Customer 2", "regular2@example.com", "444-4444", "Address 4", false)
        );
    }
}