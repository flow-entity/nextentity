package io.github.nextentity.examples.repository;

import io.github.nextentity.api.Select;
import io.github.nextentity.examples.entity.Employee;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRepository extends BaseRepository<Employee, Long> {

    @Override
    public Select<Employee> query() {
        return super.query();
    }
}
