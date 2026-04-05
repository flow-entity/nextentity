package io.github.nextentity.examples.repository;

import io.github.nextentity.api.QueryBuilder;
import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.spring.AbstractRepository;
import io.github.nextentity.spring.NextEntityContext;
import org.springframework.stereotype.Repository;

import java.util.List;

/// 部门 Repository。
///
/// 用于 {@link Department} 实体的数据访问。
///
/// ## 嵌套路径查询支持
///
/// 因为 {@link Department} 实现了 {@link io.github.nextentity.api.Entity} 接口，
/// 可以在 Employee 查询中直接访问嵌套属性：
/// ```java
/// // 在 EmployeeRepository 中
/// List<Employee> employees = employeeRepository.query()
///     .where(Employee::getDepartment).get(Department::getName).eq("技术部")
///     .list();
/// ```
@Repository
public class DepartmentRepository extends AbstractRepository<Department, Long> {

    /// 创建 Repository 实例。
    ///
    /// 通过构造器注入 NextEntityContext，自动检测实体类型和主键类型，
    /// 并初始化查询构建器和更新执行器。
    ///
    /// @param context NextEntity 上下文
    protected DepartmentRepository(NextEntityContext context) {
        super(context);
    }

    @Override
    public QueryBuilder<Department> query() {
        return super.query();
    }

    // ==================== 基本 CRUD 操作 ====================

    /// 查询所有活跃部门。
    public List<Department> findActiveDepartments() {
        return query()
                .where(Department::getActive).eq(true)
                .orderBy(Department::getName).asc()
                .list();
    }

    /// 根据ID查找部门。
    public Department findDepartmentById(Long id) {
        return query()
                .where(Department::getId).eq(id)
                .first();
    }

    // ==================== 投影 ====================

    /// 选择活跃部门的名称列表。
    public List<String> findActiveDepartmentNames() {
        return query()
                .select(Department::getName)
                .where(Department::getActive).eq(true)
                .list();
    }

    /// 查询部门信息到 DTO。
    public List<DepartmentInfo> findDepartmentInfo() {
        return query()
                .select(DepartmentInfo.class)
                .where(Department::getActive).eq(true)
                .list();
    }

    // ==================== DTO 类 ====================

    /// 部门信息 DTO。
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