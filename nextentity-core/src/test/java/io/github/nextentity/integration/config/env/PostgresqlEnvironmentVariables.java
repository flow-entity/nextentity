package io.github.nextentity.integration.config.env;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

public class PostgresqlEnvironmentVariables extends DbContainerEnvironmentVariables {

    private static final PostgreSQLContainer POSTGRESQL_CONTAINER;

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer("postgres:latest")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withEnv("POSTGRES_INITDB_ARGS", "--lc-collate=C --lc-ctype=C");
        POSTGRESQL_CONTAINER.start();
    }

    @Override
    protected JdbcDatabaseContainer<?> getContainer() {
        return POSTGRESQL_CONTAINER;
    }

    @Override
    public String name() {
        return "postgres";
    }

    @Override
    public List<String> ddl() {
        return List.of(
                "DROP TABLE IF EXISTS auto_increment_entity",
                "DROP TABLE IF EXISTS sales_order",
                "DROP TABLE IF EXISTS customer",
                "DROP TABLE IF EXISTS person_with_nested_overridden_contact",
                "DROP TABLE IF EXISTS person_with_overridden_address",
                "DROP TABLE IF EXISTS person_with_nested_address",
                "DROP TABLE IF EXISTS person_with_address",
                "DROP TABLE IF EXISTS employee",
                "DROP TABLE IF EXISTS department",
                "DROP TABLE IF EXISTS category",
                "DROP TABLE IF EXISTS lockable_entity",
                """
                        CREATE TABLE "department" (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            location VARCHAR(100),
                            budget DOUBLE PRECISION,
                            active BOOLEAN,
                            created_at TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE "employee" (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            email VARCHAR(100),
                            salary DOUBLE PRECISION,
                            active BOOLEAN,
                            status INTEGER,
                            department_id BIGINT,
                            hire_date DATE,
                            created_at TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE "lockable_entity" (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description VARCHAR(255),
                            version BIGINT,
                            created_at TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE "category" (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            parent_id BIGINT
                        )
                        """,
                """
                        CREATE TABLE "auto_increment_entity" (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description VARCHAR(255),
                            priority INTEGER,
                            active BOOLEAN,
                            created_at TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE "customer" (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            email VARCHAR(100)
                        )
                        """,
                """
                        CREATE TABLE "sales_order" (
                            id BIGINT PRIMARY KEY,
                            order_no VARCHAR(100) NOT NULL,
                            customer_id BIGINT,
                            amount DECIMAL(19,2)
                        )
                        """,
                """
                        CREATE TABLE "person_with_address" (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100),
                            street VARCHAR(100),
                            city VARCHAR(100),
                            zip_code VARCHAR(20)
                        )
                        """,
                """
                        CREATE TABLE "person_with_nested_address" (
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
                        CREATE TABLE "person_with_overridden_address" (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100),
                            addr_street VARCHAR(100),
                            addr_city VARCHAR(100),
                            zip_code VARCHAR(20)
                        )
                        """,
                """
                        CREATE TABLE "person_with_nested_overridden_contact" (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100),
                            contact_email VARCHAR(100),
                            phone VARCHAR(20),
                            deep_street VARCHAR(100),
                            city VARCHAR(100),
                            zip_code VARCHAR(20)
                        )
                        """
        );
    }
}
