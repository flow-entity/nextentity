package io.github.nextentity.examples;

import io.github.nextentity.core.QueryBuilder;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Spring Boot JPA Configuration Example.
 * <p>
 * This example demonstrates how to configure NextEntity with JPA (Hibernate)
 * in a Spring Boot application.
 *
 * <h2>Dependencies (pom.xml):</h2>
 * <pre>{@code
 * <dependencies>
 *     <!-- NextEntity -->
 *     <dependency>
 *         <groupId>io.github.flow-entity</groupId>
 *         <artifactId>nextentity-spring</artifactId>
 *         <version>2.0.0</version>
 *     </dependency>
 *
 *     <!-- Spring Boot JPA Starter -->
 *     <dependency>
 *         <groupId>org.springframework.boot</groupId>
 *         <artifactId>spring-boot-starter-data-jpa</artifactId>
 *     </dependency>
 *
 *     <!-- Database Driver (e.g., H2) -->
 *     <dependency>
 *         <groupId>com.h2database</groupId>
 *         <artifactId>h2</artifactId>
 *         <scope>runtime</scope>
 *     </dependency>
 * </dependencies>
 * }</pre>
 *
 * <h2>Application Properties (application.yml):</h2>
 * <pre>{@code
 * spring:
 *   datasource:
 *     url: jdbc:h2:mem:testdb
 *     driver-class-name: org.h2.Driver
 *     username: sa
 *     password:
 *   jpa:
 *     hibernate:
 *       ddl-auto: create-drop
 *     show-sql: true
 *     properties:
 *       hibernate:
 *         format_sql: true
 * }</pre>
 *
 * @see JpaMetamodel
 */
@Configuration
public class JpaConfigurationExample {

    /**
     * Example service using JPA configuration.
     * <p>
     * With Spring Boot auto-configuration, you can inject
     * QueryExecutor and UpdateExecutor directly.
     * <pre>{@code
     * @Service
     * public class EmployeeService {
     *
     *     private final QueryExecutor queryExecutor;
     *     private final UpdateExecutor updateExecutor;
     *     private final JpaMetamodel metamodel;
     *
     *     public EmployeeService(QueryExecutor queryExecutor,
     *                           UpdateExecutor updateExecutor) {
     *         this.queryExecutor = queryExecutor;
     *         this.updateExecutor = updateExecutor;
     *         this.metamodel = JpaMetamodel.of();
     *     }
     *
     *     public List<Employee> findActiveEmployees() {
     *         return new QueryBuilder<>(metamodel, queryExecutor, Employee.class)
     *             .where(Employee::getActive).eq(true)
     *             .getList();
     *     }
     *
     *     public void saveEmployee(Employee employee) {
     *         updateExecutor.insert(employee, Employee.class);
     *     }
     * }
     * }</pre>
     */

    // Note: In actual Spring Boot application, QueryExecutor and UpdateExecutor
    // are auto-configured by nextentity-spring module.

    /**
     * Example of manual configuration (if not using auto-configuration).
     * <p>
     * <pre>{@code
     * @Configuration
     * public class NextEntityConfig {
     *
     *     @Bean
     *     public JpaMetamodel jpaMetamodel() {
     *         return JpaMetamodel.of();
     *     }
     *
     *     @Bean
     *     public QueryExecutor queryExecutor(EntityManager entityManager) {
     *         return new JpaQueryExecutor(entityManager);
     *     }
     *
     *     @Bean
     *     public UpdateExecutor updateExecutor(EntityManager entityManager) {
     *         return new JpaUpdateExecutor(entityManager);
     *     }
     * }
     * }</pre>
     */

    /**
     * Creating a reusable QueryBuilder factory.
     * <p>
     * <pre>{@code
     * @Component
     * public class QueryFactory {
     *
     *     private final JpaMetamodel metamodel;
     *     private final QueryExecutor queryExecutor;
     *
     *     public QueryFactory(QueryExecutor queryExecutor) {
     *         this.queryExecutor = queryExecutor;
     *         this.metamodel = JpaMetamodel.of();
     *     }
     *
     *     public <T> QueryBuilder<T> query(Class<T> entityType) {
     *         return new QueryBuilder<>(metamodel, queryExecutor, entityType);
     *     }
     * }
     *
     * // Usage:
     * @Service
     * public class EmployeeService {
     *
     *     private final QueryFactory queryFactory;
     *
     *     public List<Employee> findActiveEmployees() {
     *         return queryFactory.query(Employee.class)
     *             .where(Employee::getActive).eq(true)
     *             .getList();
     *     }
     * }
     * }</pre>
     */
}