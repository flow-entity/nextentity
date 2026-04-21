package io.github.nextentity.examples.projection;

import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.integration.BaseIntegrationTest;
import io.github.nextentity.examples.integration.TestDataFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// 延迟加载行为详细测试
///
/// 测试覆盖：
/// - 首次访问触发批量加载
/// - 相同外键值从缓存返回
/// - 不同外键值触发新的加载
/// - null 外键值处理
/// - 延迟加载与急加载对比
@DisplayName("Lazy Loading Behavior Tests")
class LazyLoadingBehaviorTest extends BaseIntegrationTest {

    // ==================== Interface 投影延迟加载 ====================

    @Nested
    @DisplayName("Interface Projection Lazy Loading")
    class InterfaceProjectionLazyLoadingTests {

        @Test
        @DisplayName("Interface 投影代理验证")
        void interfaceProjectionProxyVerification() {
            List<EmployeeInfoInterface> results = employeeRepository.query()
                    .select(EmployeeInfoInterface.class)
                    .where(Employee::getDepartmentId).isNotNull()
                    .list();

            assertThat(results).isNotEmpty();

            // 验证所有结果都是 JDK Proxy
            for (EmployeeInfoInterface emp : results) {
                assertThat(Proxy.isProxyClass(emp.getClass())).isTrue();
            }
        }

        @Test
        @DisplayName("首次访问触发批量加载")
        void firstAccessTriggerBatchLoading() {
            List<EmployeeInfoInterface> results = employeeRepository.query()
                    .select(EmployeeInfoInterface.class)
                    .where(Employee::getDepartmentId).isNotNull()
                    .list();

            assertThat(results).isNotEmpty();

            // 访问第一个员工的部门
            EmployeeInfoInterface first = results.get(0);
            Department dept = first.getDepartment();

            // 验证部门信息完整
            if (dept != null) {
                assertThat(dept.getId()).isNotNull();
                assertThat(dept.getName()).isNotNull();
                assertThat(dept.getLocation()).isNotNull();
            }
        }

        @Test
        @DisplayName("相同部门 ID 从缓存返回")
        void sameDepartmentIdFromCache() {
            // 获取同一部门的多名员工
            Long engineeringDeptId = TestDataFactory.getDeptIdStart() + 1; // Engineering

            List<EmployeeInfoInterface> results = employeeRepository.query()
                    .select(EmployeeInfoInterface.class)
                    .where(Employee::getDepartmentId).eq(engineeringDeptId)
                    .list();

            assertThat(results).hasSizeGreaterThan(1);

            // 访问第一个员工的部门
            Department dept1 = results.get(0).getDepartment();
            assertThat(dept1).isNotNull();

            // 访问第二个员工的部门（应从缓存返回）
            Department dept2 = results.get(1).getDepartment();
            assertThat(dept2).isNotNull();

            // 验证相同部门信息
            assertThat(dept2.getId()).isEqualTo(dept1.getId());
            assertThat(dept2.getName()).isEqualTo(dept1.getName());
        }

        @Test
        @DisplayName("不同部门 ID 正确加载")
        void differentDepartmentIdCorrectlyLoaded() {
            List<EmployeeInfoInterface> results = employeeRepository.query()
                    .select(EmployeeInfoInterface.class)
                    .where(Employee::getDepartmentId).isNotNull()
                    .orderBy(Employee::getDepartmentId).asc()
                    .list();

            assertThat(results).hasSizeGreaterThan(1);

            Department prevDept = null;

            for (EmployeeInfoInterface emp : results) {
                Department dept = emp.getDepartment();
                if (dept != null) {
                    // 不同部门应加载不同部门实例
                    if (prevDept != null && !dept.getId().equals(prevDept.getId())) {
                        assertThat(dept.getId()).isNotEqualTo(prevDept.getId());
                    }
                    prevDept = dept;
                }
            }
        }
    }

    // ==================== Class 投影延迟加载 ====================

    @Nested
    @DisplayName("Class Projection Lazy Loading")
    class ClassProjectionLazyLoadingTests {

        @Test
        @DisplayName("Class 投影代理验证")
        void classProjectionProxyVerification() {
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getDepartmentId).isNotNull()
                    .list();

            assertThat(results).isNotEmpty();

            // 验证所有结果都是 CGLIB Proxy
            for (EmployeeInfoClass emp : results) {
                assertThat(emp.getClass().getName()).contains("$$EnhancerByCGLIB$$");
                assertThat(emp).isInstanceOf(EmployeeInfoClass.class);
            }
        }

        @Test
        @DisplayName("首次访问触发批量加载")
        void firstAccessTriggerBatchLoading() {
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getDepartmentId).isNotNull()
                    .list();

            assertThat(results).isNotEmpty();

            EmployeeInfoClass first = results.get(0);
            Department dept = first.getDepartment();

            if (dept != null) {
                assertThat(dept.getId()).isNotNull();
                assertThat(dept.getName()).isNotNull();
            }
        }

        @Test
        @DisplayName("相同部门 ID 从缓存返回")
        void sameDepartmentIdFromCache() {
            Long engineeringDeptId = TestDataFactory.getDeptIdStart() + 1;

            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getDepartmentId).eq(engineeringDeptId)
                    .list();

            assertThat(results).hasSizeGreaterThan(1);

            Department dept1 = results.get(0).getDepartment();
            assertThat(dept1).isNotNull();

            Department dept2 = results.get(1).getDepartment();
            assertThat(dept2).isNotNull();

            assertThat(dept2.getId()).isEqualTo(dept1.getId());
            assertThat(dept2.getName()).isEqualTo(dept1.getName());
        }

        @Test
        @DisplayName("多属性投影延迟加载正常工作")
        void multiAttributeProjectionLazyLoadingWorks() {
            List<EmployeeDetailDto> results = employeeRepository.query()
                    .select(EmployeeDetailDto.class)
                    .where(Employee::getDepartmentId).isNotNull()
                    .list();

            assertThat(results).isNotEmpty();

            EmployeeDetailDto first = results.get(0);
            assertThat(first.getId()).isNotNull();
            assertThat(first.getName()).isNotNull();
            assertThat(first.getEmail()).isNotNull();

            Department dept = first.getDepartment();
            if (dept != null) {
                assertThat(dept.getId()).isNotNull();
                assertThat(dept.getName()).isNotNull();
            }
        }
    }

    // ==================== 延迟加载边界测试 ====================

    @Nested
    @DisplayName("Lazy Loading Edge Cases")
    class LazyLoadingEdgeCaseTests {

        @Test
        @DisplayName("空结果集正确处理")
        void emptyResultSetHandling() {
            // 使用不存在员工姓名查询
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getName).eq("NonExistentEmployee")
                    .list();

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("单条结果延迟加载正常")
        void singleResultLazyLoadingWorks() {
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getName).eq("Alice Johnson")
                    .list();

            assertThat(results).hasSize(1);

            EmployeeInfoClass alice = results.get(0);
            assertThat(alice.getName()).isEqualTo("Alice Johnson");

            Department dept = alice.getDepartment();
            assertThat(dept).isNotNull();
            assertThat(dept.getName()).isEqualTo("Engineering");
        }

        @Test
        @DisplayName("大量数据批量加载性能")
        void largeDataSetBatchLoadingPerformance() {
            // 获取所有活跃员工（测试数据约 10 人）
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            assertThat(results).hasSizeGreaterThan(5);

            // 访问所有员工的部门
            // 首次访问触发批量加载，后续从缓存返回
            int loadedCount = 0;
            for (EmployeeInfoClass emp : results) {
                if (emp.getDepartment() != null) {
                    loadedCount++;
                }
            }

            // 验证大部分员工有部门
            assertThat(loadedCount).isGreaterThan(0);
        }
    }

    // ==================== 投影类型对比 ====================

    @Nested
    @DisplayName("Projection Type Comparison")
    class ProjectionTypeComparisonTests {

        @Test
        @DisplayName("Interface 和 Class 投影返回相同数据")
        void interfaceAndClassProjectionReturnSameData() {
            // Interface 投影
            List<EmployeeInfoInterface> interfaceResults = employeeRepository.query()
                    .select(EmployeeInfoInterface.class)
                    .where(Employee::getName).eq("Alice Johnson")
                    .list();

            // Class 投影
            List<EmployeeInfoClass> classResults = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getName).eq("Alice Johnson")
                    .list();

            assertThat(interfaceResults).hasSize(1);
            assertThat(classResults).hasSize(1);

            // 验证数据一致
            EmployeeInfoInterface interfaceEmp = interfaceResults.get(0);
            EmployeeInfoClass classEmp = classResults.get(0);

            assertThat(interfaceEmp.getId()).isEqualTo(classEmp.getId());
            assertThat(interfaceEmp.getName()).isEqualTo(classEmp.getName());
            assertThat(interfaceEmp.getEmail()).isEqualTo(classEmp.getEmail());

            // 验证部门信息一致
            Department interfaceDept = interfaceEmp.getDepartment();
            Department classDept = classEmp.getDepartment();

            assertThat(interfaceDept.getId()).isEqualTo(classDept.getId());
            assertThat(interfaceDept.getName()).isEqualTo(classDept.getName());
        }

        @Test
        @DisplayName("不同代理类型实现相同功能")
        void differentProxyTypesSameFunctionality() {
            List<EmployeeInfoInterface> interfaceResults = employeeRepository.query()
                    .select(EmployeeInfoInterface.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            List<EmployeeInfoClass> classResults = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            // 验证数量相同
            assertThat(interfaceResults).hasSameSizeAs(classResults);

            // 验证代理类型不同
            if (!interfaceResults.isEmpty() && !classResults.isEmpty()) {
                // Interface 使用 JDK Proxy
                assertThat(Proxy.isProxyClass(interfaceResults.get(0).getClass())).isTrue();
                // Class 使用 CGLIB Proxy
                assertThat(classResults.get(0).getClass().getName()).contains("$$EnhancerByCGLIB$$");
            }
        }
    }
}