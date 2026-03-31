# NextEntity API Reference

Complete API reference for NextEntity SQL DSL framework.

## Core Interfaces

### Repository\<ID, T\>

The main interface for data access operations.

```java
public interface Repository<ID, T> extends Select<T>, Update<T>, EntityRoot<T> {
}
```

### Select\<T\>

Interface for SELECT operations.

| Method | Description |
|--------|-------------|
| `select(Class<R> projectionType)` | Select into DTO class |
| `select(PathRef<T, R> path)` | Select single field |
| `select(PathRef<T, A> a, PathRef<T, B> b)` | Select two fields as Tuple2 |
| `select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c)` | Select three fields as Tuple3 |
| `select(...)` | Select up to 10 fields |
| `selectDistinct(...)` | Select distinct values |

### Update\<T\>

Interface for INSERT, UPDATE, DELETE operations.

| Method | Description |
|--------|-------------|
| `insert(T entity)` | Insert single entity |
| `insertAll(Iterable<T> entities)` | Batch insert |
| `update(T entity)` | Update single entity |
| `updateAll(Iterable<T> entities)` | Batch update |
| `delete(T entity)` | Delete single entity |
| `deleteAll(Iterable<T> entities)` | Batch delete |
| `doInTransaction(Runnable command)` | Execute in transaction |

---

## Expression Operators

### ExpressionBuilder\<T, U, B\>

Base interface for building conditions.

#### Equality Operators

| Method | Description |
|--------|-------------|
| `eq(U value)` | Equals value |
| `eq(Expression<T, U> expression)` | Equals expression |
| `eqIfNotNull(U value)` | Equals if value is not null |
| `ne(U value)` | Not equals value |
| `ne(Expression<T, U> expression)` | Not equals expression |
| `neIfNotNull(U value)` | Not equals if value is not null |

#### Comparison Operators

| Method | Description |
|--------|-------------|
| `gt(U value)` | Greater than |
| `ge(U value)` | Greater than or equal |
| `lt(U value)` | Less than |
| `le(U value)` | Less than or equal |
| `gtIfNotNull(U value)` | GT if value is not null |
| `geIfNotNull(U value)` | GE if value is not null |
| `ltIfNotNull(U value)` | LT if value is not null |
| `leIfNotNull(U value)` | LE if value is not null |

#### Range Operators

| Method | Description |
|--------|-------------|
| `between(U l, U r)` | Value in range [l, r] |
| `notBetween(U l, U r)` | Value not in range |

#### IN Operators

| Method | Description |
|--------|-------------|
| `in(U... values)` | Value in array |
| `in(Collection<? extends U> values)` | Value in collection |
| `notIn(U... values)` | Value not in array |
| `notIn(Collection<? extends U> values)` | Value not in collection |

#### NULL Operators

| Method | Description |
|--------|-------------|
| `isNull()` | Value is NULL |
| `isNotNull()` | Value is NOT NULL |

---

### NumberOperator\<T, U, B\>

Numeric-specific operators.

| Method | Description |
|--------|-------------|
| `add(U value)` | Addition |
| `subtract(U value)` | Subtraction |
| `multiply(U value)` | Multiplication |
| `divide(U value)` | Division |
| `mod(U value)` | Modulo |
| `add(Expression<T, U> expression)` | Add expression |
| `subtract(Expression<T, U> expression)` | Subtract expression |
| `multiply(Expression<T, U> expression)` | Multiply expression |
| `divide(Expression<T, U> expression)` | Divide expression |

---

### StringOperator\<T, B\>

String-specific operators.

#### Pattern Matching

| Method | Description |
|--------|-------------|
| `like(String value)` | SQL LIKE pattern |
| `startsWith(String prefix)` | Starts with prefix |
| `endsWith(String suffix)` | Ends with suffix |
| `contains(String text)` | Contains text |
| `notLike(String value)` | NOT LIKE pattern |

#### Conditional Pattern Matching

| Method | Description |
|--------|-------------|
| `likeIfNotNull(String value)` | LIKE if value is not null |
| `startsWithIfNotNull(String prefix)` | Starts with if not null |
| `endsWithIfNotNull(String suffix)` | Ends with if not null |
| `containsIfNotNull(String text)` | Contains if not null |
| `likeIfNotEmpty(String value)` | LIKE if value is not empty |
| `containsIfNotEmpty(String text)` | Contains if not empty |

#### String Functions

| Method | Description |
|--------|-------------|
| `lower()` | Convert to lowercase |
| `upper()` | Convert to uppercase |
| `trim()` | Remove leading/trailing spaces |
| `substring(int offset, int length)` | Extract substring |
| `length()` | Get string length |

---

## Query Building

### BaseWhereStep\<T, U\>

Interface for building WHERE clauses.

```java
// Add condition
.where(Employee::getName).eq("John")

// Multiple conditions (AND)
.where(Employee::getActive).eq(true)
.where(Employee::getSalary).gt(50000.0)

// Add expression directly
.where(predicate -> predicate
    .or(Employee::getStatus).eq(ACTIVE)
    .or(Employee::getStatus).eq(ON_LEAVE))
```

### OrderByStep\<T, U\>

Interface for ordering results.

```java
// Single sort
.orderBy(Employee::getName).asc()
.orderBy(Employee::getSalary).desc()

// Multiple sorts
.orderBy(Employee::getDepartmentId).asc()
.orderBy(Employee::getSalary).desc()
```

### FetchStep\<T\>

Interface for fetching associations.

```java
// Fetch single association
.fetch(Employee::getDepartment)

// Fetch multiple associations
.fetch(Employee::getDepartment)
.fetch(Employee::getManager)
```

---

## Result Retrieval

### Terminal Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getList()` | `List<U>` | Get all results |
| `getList(int offset, int limit)` | `List<U>` | Get paginated results |
| `getFirst()` | `U` | Get first result (null if empty) |
| `getSingle()` | `U` | Get single result (for aggregations) |
| `count()` | `long` | Count results |
| `slice(int offset, int limit)` | `Slice<U>` | Get slice with metadata |
| `stream()` | `Stream<U>` | Get result stream |

---

## Aggregation Functions

Use `path()` method reference to build aggregate expressions:

```java
// Available aggregate functions
path(Employee::getSalary).sum()    // Sum
path(Employee::getSalary).avg()    // Average
path(Employee::getSalary).max()    // Maximum
path(Employee::getSalary).min()    // Minimum
```

### Aggregation Examples

```java
// Sum with conditions
double totalSalary = query()
    .select(path(Employee::getSalary).sum())
    .where(Employee::getActive).eq(true)
    .getSingle();

// Count
long count = query()
    .where(Employee::getActive).eq(true)
    .count();

// Count distinct
long distinctCount = query()
    .selectDistinct(Employee::getDepartmentId)
    .count();
```

### Slice\<T\>

Pagination result wrapper.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `data()` | `List<T>` | Result content |
| `hasNext()` | `boolean` | Has more results |
| `getNumber()` | `int` | Current page number |
| `getSize()` | `int` | Page size |
| `getNumberOfElements()` | `int` | Elements in current page |

---

## Tuple Types

Type-safe tuples for multi-field selection.

### Tuple2\<A, B\>

```java
Tuple2<String, Double> tuple = ...;
String first = tuple.get0();
Double second = tuple.get1();
```

### Tuple3\<A, B, C\> to Tuple10\<...\>

```java
Tuple3<String, String, Double> tuple = ...;
String name = tuple.get0();
String email = tuple.get1();
Double salary = tuple.get2();
```

---

## Repository Pattern

### AbstractRepository\<T, ID\>

Base class for creating type-safe repositories.

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {
    // Custom methods can be added here
}

@Repository
public class DepartmentRepository extends AbstractRepository<Department, Long> {
}
```

### Usage

```java
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findActiveEmployees() {
        return employeeRepository.query()
            .where(Employee::getActive).eq(true)
            .orderBy(Employee::getName).asc()
            .getList();
    }

    public void giveRaise(Long id, double percentage) {
        Employee employee = employeeRepository.query()
            .where(Employee::getId).eq(id)
            .getFirst();

        if (employee != null) {
            employee.setSalary(employee.getSalary() * (1 + percentage));
            employeeRepository.update(employee);
        }
    }
}
```

---

## Path References

### PathRef\<T, U\>

Functional interface for type-safe property references.

```java
@FunctionalInterface
public interface PathRef<T, U> {
    U apply(T entity);
}
```

### Specialized Path References

```java
// Number path reference
public interface NumberRef<T, U extends Number> extends PathRef<T, U> {}

// String path reference
public interface StringRef<T> extends PathRef<T, String> {}

// Boolean path reference
public interface BooleanRef<T> extends PathRef<T, Boolean> {}
```

---

## Example Usage Patterns

### Basic Query

```java
List<Employee> employees = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .orderBy(Employee::getName).asc()
    .getList();
```

### Projection

```java
List<Tuple2<String, Double>> results = employeeRepository.query()
    .select(Employee::getName, Employee::getSalary)
    .where(Employee::getActive).eq(true)
    .getList();
```

### Complex Conditions

```java
List<Employee> employees = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .where(Employee::getStatus).in(ACTIVE, ON_LEAVE)
    .where(Employee::getSalary).between(40000.0, 80000.0)
    .where(Employee::getName).containsIfNotEmpty(name)
    .orderBy(Employee::getDepartmentId).asc()
    .orderBy(Employee::getSalary).desc()
    .getList(0, 100);
```

### Batch Operations

```java
// Batch insert
employeeRepository.insertAll(newEmployees);

// Batch update
employeeRepository.updateAll(updatedEmployees);

// Transaction
employeeRepository.doInTransaction(() -> {
    departmentRepository.insert(department);
    employeeRepository.insertAll(employees);
});
```