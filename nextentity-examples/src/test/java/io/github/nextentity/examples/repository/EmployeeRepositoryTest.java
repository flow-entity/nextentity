package io.github.nextentity.examples.repository;

import io.github.nextentity.examples.NextEntityExampleApplication;
import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.examples.integration.TestDataFactory;
import io.github.nextentity.examples.model.EmployeeModel;
import io.github.nextentity.examples.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;


@SpringBootTest(classes = NextEntityExampleApplication.class)
class EmployeeRepositoryTest {

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    EmployeeService employeeService;

    @Test
    void findModel() {
        // Given
        Long newDeptId = TestDataFactory.getDeptIdStart() + 500L;
        Long newEmpId = TestDataFactory.getEmpIdStart() + 5000L;

        Department dept = new Department(newDeptId, "New Department", "Building X", 100000.0, true);
        Employee emp = new Employee(newEmpId, "New Employee", "new@transaction.com",
                new BigDecimal("60000.00"), true, EmployeeStatus.ACTIVE, newDeptId, LocalDate.now());

        employeeService.createEmployeeWithDepartment(emp, dept);

        List<EmployeeModel> list = employeeRepository.query()
                .select(EmployeeModel.class)
                .list();
        assertFalse(list.isEmpty());

    }

}