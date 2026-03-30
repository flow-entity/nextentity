package io.github.nextentity.examples.repository;

import io.github.nextentity.api.Select;
import io.github.nextentity.examples.entity.Department;
import org.springframework.stereotype.Repository;

@Repository
public class DepartmentRepository extends BaseRepository<Department, Long> {

    @Override
    public Select<Department> query() {
        return super.query();
    }
}
