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
                        """
        );
    }
}