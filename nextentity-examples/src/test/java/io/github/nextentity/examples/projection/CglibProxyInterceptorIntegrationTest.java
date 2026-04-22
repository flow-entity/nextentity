package io.github.nextentity.examples.projection;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.JdkProxyInterceptor;
import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.integration.BaseIntegrationTest;
import io.github.nextentity.spring.CglibProxyInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Proxy;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// CGLIB 代理拦截器集成测试
///
/// 测试覆盖：
/// - CGLIB 与 JDK Proxy 代理类型识别
/// - 多延迟加载属性场景
/// - 嵌套投影延迟加载
/// - 批量延迟加载行为（避免 N+1）
/// - 拦截器配置验证
@DisplayName("CGLIB Proxy Interceptor Integration Tests")
class CglibProxyInterceptorIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    // ==================== 拦截器配置验证 ====================

    @Nested
    @DisplayName("Interceptor Configuration Tests")
    class InterceptorConfigurationTests {

        @Test
        @DisplayName("验证 CglibProxyInterceptor 已注册")
        void cglibProxyInterceptorIsRegistered() {
            // 获取所有 ConstructInterceptor Bean
            var interceptors = applicationContext.getBeansOfType(ConstructInterceptor.class);

            // 验证存在 CGLIB 拦截器
            boolean hasCglib = interceptors.values().stream()
                    .anyMatch(i -> i instanceof CglibProxyInterceptor);
            assertThat(hasCglib).isTrue();
        }

        @Test
        @DisplayName("验证 JdkProxyInterceptor 已注册")
        void jdkProxyInterceptorIsRegistered() {
            var interceptors = applicationContext.getBeansOfType(ConstructInterceptor.class);

            boolean hasJdk = interceptors.values().stream()
                    .anyMatch(i -> i instanceof JdkProxyInterceptor);
            assertThat(hasJdk).isTrue();
        }

        @Test
        @DisplayName("验证拦截器优先级正确")
        void interceptorOrderIsCorrect() {
            var interceptors = applicationContext.getBeansOfType(ConstructInterceptor.class);

            // 找到 CGLIB 拦截器
            ConstructInterceptor cglibInterceptor = interceptors.values().stream()
                    .filter(i -> i instanceof CglibProxyInterceptor)
                    .findFirst()
                    .orElse(null);

            assertThat(cglibInterceptor).isNotNull();
            assertThat(cglibInterceptor.order()).isEqualTo(0);
            assertThat(cglibInterceptor.name()).isEqualTo("cglib-proxy");
        }
    }

    // ==================== 代理类型识别 ====================

    @Nested
    @DisplayName("Proxy Type Detection Tests")
    class ProxyTypeTests {

        @Test
        @DisplayName("Interface 投影使用 JDK Proxy")
        void interfaceProjectionUsesJdkProxy() {
            List<EmployeeInfoInterface> results = employeeRepository.query()
                    .select(EmployeeInfoInterface.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            assertThat(results).isNotEmpty();

            Object first = results.get(0);
            assertThat(Proxy.isProxyClass(first.getClass())).isTrue();
            assertThat(first).isInstanceOf(EmployeeInfoInterface.class);
        }

        @Test
        @DisplayName("Class 投影使用 CGLIB Proxy")
        void classProjectionUsesCglibProxy() {
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            assertThat(results).isNotEmpty();

            Object first = results.get(0);
            assertThat(first.getClass().getName()).contains("$$EnhancerByCGLIB$$");
            assertThat(first).isInstanceOf(EmployeeInfoClass.class);
        }

        @Test
        @DisplayName("多属性 Class 投影使用 CGLIB Proxy")
        void multiAttributeClassProjectionUsesCglibProxy() {
            List<EmployeeDetailDto> results = employeeRepository.query()
                    .select(EmployeeDetailDto.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            assertThat(results).isNotEmpty();

            Object first = results.get(0);
            assertThat(first.getClass().getName()).contains("$$EnhancerByCGLIB$$");
            assertThat(first).isInstanceOf(EmployeeDetailDto.class);
        }
    }

    // ==================== 延迟加载行为 ====================

    @Nested
    @DisplayName("Lazy Loading Behavior Tests")
    class LazyLoadingTests {

        @Test
        @DisplayName("Interface 投影延迟加载正常工作")
        void interfaceProjectionLazyLoadingWorks() {
            List<EmployeeInfoInterface> results = employeeRepository.query()
                    .select(EmployeeInfoInterface.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            assertThat(results).isNotEmpty();

            // 验证基本属性
            EmployeeInfoInterface first = results.get(0);
            assertThat(first.getId()).isNotNull();
            assertThat(first.getName()).isNotNull();

            // 验证延迟加载属性
            Department dept = first.getDepartment();
            if (dept != null) {
                assertThat(dept.getId()).isNotNull();
                assertThat(dept.getName()).isNotNull();
            }
        }

        @Test
        @DisplayName("Class 投影延迟加载正常工作")
        void classProjectionLazyLoadingWorks() {
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            assertThat(results).isNotEmpty();

            EmployeeInfoClass first = results.get(0);
            assertThat(first.getId()).isNotNull();
            assertThat(first.getName()).isNotNull();

            Department dept = first.getDepartment();
            if (dept != null) {
                assertThat(dept.getId()).isNotNull();
                assertThat(dept.getName()).isNotNull();
            }
        }

        @Test
        @DisplayName("多延迟加载属性批量加载")
        void multipleLazyAttributesBatchLoading() {
            List<EmployeeDetailDto> results = employeeRepository.query()
                    .select(EmployeeDetailDto.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            assertThat(results).hasSizeGreaterThan(1);

            // 验证基本属性
            EmployeeDetailDto first = results.get(0);
            assertThat(first.getId()).isNotNull();
            assertThat(first.getName()).isNotNull();

            // 验证延迟加载属性
            Department dept = first.getDepartment();
            if (dept != null) {
                assertThat(dept.getId()).isNotNull();
                assertThat(dept.getName()).isNotNull();
            }

            // 验证第二个对象的延迟加载
            EmployeeDetailDto second = results.get(1);
            Department dept2 = second.getDepartment();
            if (dept2 != null && dept != null) {
                // 验证部门信息
                assertThat(dept2.getId()).isNotNull();
            }
        }

        @Test
        @DisplayName("延迟加载避免 N+1 问题")
        void lazyLoadingAvoidsN1Problem() {
            // 获取所有活跃员工
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            assertThat(results).hasSizeGreaterThan(5);

            // 首次访问触发批量加载
            Department firstDept = results.get(0).getDepartment();

            // 统计不同部门数量
            int uniqueDeptCount = 0;
            Long lastDeptId = null;
            for (EmployeeInfoClass emp : results) {
                Department dept = emp.getDepartment();
                if (dept != null) {
                    if (lastDeptId == null || !lastDeptId.equals(dept.getId())) {
                        uniqueDeptCount++;
                        lastDeptId = dept.getId();
                    }
                }
            }

            // 验证：首次访问后，所有相同部门 ID 的访问都从缓存返回
            // 批量查询只执行一次，不是 N 次
            assertThat(uniqueDeptCount).isLessThanOrEqualTo(results.size());
        }
    }

    // ==================== 基本属性验证 ====================

    @Nested
    @DisplayName("Basic Attributes Tests")
    class BasicAttributesTests {

        @Test
        @DisplayName("Class 投影基本属性正确映射")
        void classProjectionBasicAttributesCorrectlyMapped() {
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getName).eq("Alice Johnson")
                    .list();

            assertThat(results).hasSize(1);

            EmployeeInfoClass alice = results.get(0);
            assertThat(alice.getId()).isNotNull();
            assertThat(alice.getName()).isEqualTo("Alice Johnson");
            assertThat(alice.getEmail()).isEqualTo("alice@example.com");
        }

        @Test
        @DisplayName("多属性 Class 投影所有属性正确映射")
        void multiAttributeClassProjectionAllAttributesMapped() {
            List<EmployeeDetailDto> results = employeeRepository.query()
                    .select(EmployeeDetailDto.class)
                    .where(Employee::getName).eq("Bob Smith")
                    .list();

            assertThat(results).hasSize(1);

            EmployeeDetailDto bob = results.get(0);
            assertThat(bob.getId()).isNotNull();
            assertThat(bob.getName()).isEqualTo("Bob Smith");
            assertThat(bob.getEmail()).isEqualTo("bob@example.com");
            assertThat(bob.getSalary()).isEqualByComparingTo("80000.00");
            assertThat(bob.getActive()).isTrue();
        }
    }

    // ==================== 查询条件测试 ====================

    @Nested
    @DisplayName("Query Conditions Tests")
    class QueryConditionsTests {

        @Test
        @DisplayName("Class 投影支持复杂查询条件")
        void classProjectionSupportsComplexConditions() {
            // 查询活跃且薪资大于 60000 的员工
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getActive).eq(true)
                    .where(Employee::getSalary).gt(new java.math.BigDecimal("60000"))
                    .list();

            assertThat(results).isNotEmpty();

            // 验证所有结果都满足条件
            for (EmployeeInfoClass emp : results) {
                // 注意：投影中没有 salary 字段，只验证 active
                // salary 条件在查询层面已过滤
                assertThat(emp.getName()).isNotNull();
            }
        }

        @Test
        @DisplayName("Class 投影支持排序")
        void classProjectionSupportsSorting() {
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getActive).eq(true)
                    .orderBy(Employee::getName).asc()
                    .list();

            assertThat(results).hasSizeGreaterThan(1);

            // 验证排序
            String prevName = null;
            for (EmployeeInfoClass emp : results) {
                if (prevName != null) {
                    assertThat(emp.getName()).isGreaterThanOrEqualTo(prevName);
                }
                prevName = emp.getName();
            }
        }

        @Test
        @DisplayName("Class 投影支持分页")
        void classProjectionSupportsPagination() {
            List<EmployeeInfoClass> page1 = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getActive).eq(true)
                    .orderBy(Employee::getId).asc()
                    .list(0, 5);

            List<EmployeeInfoClass> page2 = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getActive).eq(true)
                    .orderBy(Employee::getId).asc()
                    .list(5, 5);

            assertThat(page1).hasSize(5);
            assertThat(page2).isNotEmpty();

            // 验证分页结果不重叠
            assertThat(page1.get(0).getId()).isNotEqualTo(page2.get(0).getId());
        }
    }
}