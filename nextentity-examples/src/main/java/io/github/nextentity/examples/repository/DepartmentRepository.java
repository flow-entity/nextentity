package io.github.nextentity.examples.repository;

import io.github.nextentity.api.Select;
import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepartmentRepository extends BaseRepository<Department, Long> {

    @Override
    public Select<Department> query() {
        return super.query();
    }

    // ==================== Basic CRUD Operations ====================

    /// Query all active departments
    public List<Department> findActiveDepartments() {
        return query()
                .where(Department::getActive).eq(true)
                .orderBy(Department::getName).asc()
                .getList();
    }

    /// Find department by ID
    public Department findDepartmentById(Long id) {
        return query()
                .where(Department::getId).eq(id)
                .getFirst();
    }

    // ==================== Projection ====================

    /// Select from different entity types
    public List<String> findActiveDepartmentNames() {
        return query()
                .select(Department::getName)
                .where(Department::getActive).eq(true)
                .getList();
    }

    /// Select department info into DTO
    public List<DepartmentInfo> findDepartmentInfo() {
        return query()
                .select(DepartmentInfo.class)
                .where(Department::getActive).eq(true)
                .getList();
    }

    // ==================== DTO Classes ====================

    /// DTO for department info
    public static class DepartmentInfo {
        private Long id;
        private String name;
        private String location;
        private Double budget;

        public DepartmentInfo() {}

        public DepartmentInfo(Long id, String name, String location, Double budget) {
            this.id = id;
            this.name = name;
            this.location = location;
            this.budget = budget;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Double getBudget() { return budget; }
        public void setBudget(Double budget) { this.budget = budget; }
    }
}