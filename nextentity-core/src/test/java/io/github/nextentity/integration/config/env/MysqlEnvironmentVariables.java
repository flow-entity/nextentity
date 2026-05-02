package io.github.nextentity.integration.config.env;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.mysql.MySQLContainer;

import java.util.List;

public class MysqlEnvironmentVariables extends DbContainerEnvironmentVariables {

    private static final MySQLContainer MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer("mysql:latest")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        MYSQL_CONTAINER.start();
    }

    @Override
    protected JdbcDatabaseContainer<?> getContainer() {
        return MYSQL_CONTAINER;
    }

    @Override
    public String name() {
        return "mysql";
    }

    @Override
    public List<String> ddl() {
        return List.of(
                "DROP TABLE IF EXISTS auto_increment_entity",
                "DROP TABLE IF EXISTS sales_order",
                "DROP TABLE IF EXISTS customer",
                "DROP TABLE IF EXISTS person_with_cross_layer_embedded",
                "DROP TABLE IF EXISTS person_with_nested_overridden_contact",
                "DROP TABLE IF EXISTS person_with_overridden_address",
                "DROP TABLE IF EXISTS person_with_nested_address",
                "DROP TABLE IF EXISTS person_with_address",
                "DROP TABLE IF EXISTS employee",
                "DROP TABLE IF EXISTS department",
                "DROP TABLE IF EXISTS category",
                "DROP TABLE IF EXISTS lockable_entity",
                """
                        CREATE TABLE department (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            location VARCHAR(100),
                            budget DOUBLE,
                            active BOOLEAN,
                            created_at TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE employee (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            email VARCHAR(100),
                            salary DOUBLE,
                            active BOOLEAN,
                            status INT,
                            department_id BIGINT,
                            hire_date DATE,
                            created_at TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE lockable_entity (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description VARCHAR(255),
                            version BIGINT,
                            created_at TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE category (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            parent_id BIGINT
                        )
                        """,
                """
                        CREATE TABLE auto_increment_entity (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(100) NOT NULL,
                            description VARCHAR(255),
                            priority INT,
                            active BOOLEAN,
                            created_at TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE customer (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            email VARCHAR(100)
                        )
                        """,
                """
                        CREATE TABLE sales_order (
                            id BIGINT PRIMARY KEY,
                            order_no VARCHAR(100) NOT NULL,
                            customer_id BIGINT,
                            amount DECIMAL(19,2)
                        )
                        """,
                """
                        CREATE TABLE person_with_address (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100),
                            street VARCHAR(100),
                            city VARCHAR(100),
                            zip_code VARCHAR(20)
                        )
                        """,
                """
                        CREATE TABLE person_with_nested_address (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100),
                            email VARCHAR(100),
                            phone VARCHAR(20),
                            street VARCHAR(100),
                            city VARCHAR(100),
                            zip_code VARCHAR(20)
                        )
                        """,
                """
                        CREATE TABLE person_with_overridden_address (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100),
                            addr_street VARCHAR(100),
                            addr_city VARCHAR(100),
                            zip_code VARCHAR(20)
                        )
                        """,
                """
                        CREATE TABLE person_with_nested_overridden_contact (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100),
                            contact_email VARCHAR(100),
                            phone VARCHAR(20),
                            deep_street VARCHAR(100),
                            city VARCHAR(100),
                            zip_code VARCHAR(20)
                        )
                        """,
                """
                        CREATE TABLE person_with_cross_layer_embedded (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100),
                            city VARCHAR(100),
                            street VARCHAR(100),
                            code VARCHAR(20),
                            alt_code VARCHAR(20)
                        )
                        """
        );
    }
}
