package io.github.nextentity.integration.config.env;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.mssqlserver.MSSQLServerContainer;

import java.util.List;

public class SqlServerEnvironmentVariables extends DbContainerEnvironmentVariables {

    private static final MSSQLServerContainer SQLSERVER_CONTAINER;

    static {
        SQLSERVER_CONTAINER = new MSSQLServerContainer("mcr.microsoft.com/mssql/server:latest")
                .acceptLicense();
        SQLSERVER_CONTAINER.start();
    }

    @Override
    protected JdbcDatabaseContainer<?> getContainer() {
        return SQLSERVER_CONTAINER;
    }

    @Override
    public String name() {
        return "sqlserver";
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
                            name NVARCHAR(100) NOT NULL,
                            location NVARCHAR(100),
                            budget FLOAT,
                            active BIT,
                            created_at DATETIME2
                        )
                        """,
                """
                        CREATE TABLE employee (
                            id BIGINT PRIMARY KEY,
                            name NVARCHAR(100) NOT NULL,
                            email NVARCHAR(100),
                            salary FLOAT,
                            active BIT,
                            status INT,
                            department_id BIGINT,
                            hire_date DATE,
                            created_at DATETIME2
                        )
                        """,
                """
                        CREATE TABLE lockable_entity (
                            id BIGINT PRIMARY KEY,
                            name NVARCHAR(100) NOT NULL,
                            description NVARCHAR(255),
                            version BIGINT,
                            created_at DATETIME2
                        )
                        """,
                """
                        CREATE TABLE category (
                            id BIGINT PRIMARY KEY,
                            name NVARCHAR(100) NOT NULL,
                            parent_id BIGINT
                        )
                        """,
                """
                        CREATE TABLE auto_increment_entity (
                            id BIGINT PRIMARY KEY IDENTITY(1,1),
                            name NVARCHAR(100) NOT NULL,
                            description NVARCHAR(255),
                            priority INT,
                            active BIT,
                            created_at DATETIME2
                        )
                        """,
                """
                        CREATE TABLE customer (
                            id BIGINT PRIMARY KEY,
                            name NVARCHAR(100) NOT NULL,
                            email NVARCHAR(100)
                        )
                        """,
                """
                        CREATE TABLE sales_order (
                            id BIGINT PRIMARY KEY,
                            order_no NVARCHAR(100) NOT NULL,
                            customer_id BIGINT,
                            amount DECIMAL(19,2)
                        )
                        """,
                """
                        CREATE TABLE person_with_address (
                            id BIGINT PRIMARY KEY,
                            name NVARCHAR(100),
                            street NVARCHAR(100),
                            city NVARCHAR(100),
                            zip_code NVARCHAR(20)
                        )
                        """,
                """
                        CREATE TABLE person_with_nested_address (
                            id BIGINT PRIMARY KEY,
                            name NVARCHAR(100),
                            email NVARCHAR(100),
                            phone NVARCHAR(20),
                            street NVARCHAR(100),
                            city NVARCHAR(100),
                            zip_code NVARCHAR(20)
                        )
                        """,
                """
                        CREATE TABLE person_with_overridden_address (
                            id BIGINT PRIMARY KEY,
                            name NVARCHAR(100),
                            addr_street NVARCHAR(100),
                            addr_city NVARCHAR(100),
                            zip_code NVARCHAR(20)
                        )
                        """,
                """
                        CREATE TABLE person_with_nested_overridden_contact (
                            id BIGINT PRIMARY KEY,
                            name NVARCHAR(100),
                            contact_email NVARCHAR(100),
                            phone NVARCHAR(20),
                            deep_street NVARCHAR(100),
                            city NVARCHAR(100),
                            zip_code NVARCHAR(20)
                        )
                        """,
                """
                        CREATE TABLE person_with_cross_layer_embedded (
                            id BIGINT PRIMARY KEY,
                            name NVARCHAR(100),
                            city NVARCHAR(100),
                            street NVARCHAR(100),
                            code NVARCHAR(20),
                            alt_code NVARCHAR(20)
                        )
                        """
        );
    }
}