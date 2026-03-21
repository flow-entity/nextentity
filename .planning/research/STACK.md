# Technology Stack

**Project:** NextEntity - Java SQL DSL Framework
**Researched:** 2026-03-22

## Recommended Stack

### Core Framework
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| Java | 17+ | Core language/runtime | LTS version with modern features (records, pattern matching) |
| Maven | 3.9+ | Build system | Industry standard for Java projects, excellent dependency management |
| JUnit | 5.10+ | Testing framework | Modern Java testing, supports parameterized tests needed for multi-database testing |
| Lombok | 1.18.30+ | Code generation | Reduces boilerplate, aligns with existing project setup |

### Database Connectivity
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| JDBC API | 4.3+ | Database connectivity | Standard Java database API, essential for SQL DSL |
| MySQL Connector/J | 8.2+ | MySQL driver | Industry standard for MySQL connectivity |
| Microsoft JDBC Driver | 12.6+ | SQL Server driver | Latest stable for SQL Server connectivity |
| PostgreSQL JDBC | 42.6+ | PostgreSQL driver | Current stable for PostgreSQL connectivity |

### Core Libraries
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| jOOQ | 3.18+ | Reference implementation | Study for DSL patterns and API design |
| Apache Commons Lang | 3.12+ | Utility functions | String manipulation, object utilities |
| SLF4J | 2.0+ | Logging facade | Standard logging abstraction |
| HikariCP | 5.0+ | Connection pooling | High-performance connection pool |

### Spring Integration
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| Spring Boot | 3.2.0+ | Auto-configuration | Aligns with existing project setup, enables easy integration |
| Spring Data | 3.2+ | Repository abstractions | Reference for Repository interface design |

### Testing & Infrastructure
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| Testcontainers | 1.19+ | Integration testing | Multi-database testing support |
| Mockito | 5.10+ | Mocking framework | Test isolation for unit tests |
| AssertJ | 3.24+ | Fluent assertions | Better test readability |

## Alternatives Considered

| Category | Recommended | Alternative | Why Not |
|----------|-------------|-------------|---------|
| Java Version | 17+ | Java 11 | Java 17 provides more modern language features (records, sealed classes) |
| Build Tool | Maven | Gradle | Maven is more prevalent in enterprise environments, simpler for multi-module projects |
| SQL DSL | Internal | MyBatis | MyBatis is more ORM-focused, not pure SQL DSL |
| Connection Pool | HikariCP | Commons DBCP | HikariCP has superior performance characteristics |

## Installation

```bash
# Core dependencies
<dependencies>
    <!-- Java 17+ -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
    </dependency>

    <!-- JDBC Drivers -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.2.0</version>
    </dependency>

    <dependency>
        <groupId>com.microsoft.sqlserver</groupId>
        <artifactId>mssql-jdbc</artifactId>
        <version>12.6.1.jre11</version>
    </dependency>

    <!-- Test Dependencies -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.3</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mysql</artifactId>
        <version>1.19.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Sources

- Project context provided in PROJECT.md
- Java platform evolution trends 2025
- Industry best practices for SQL DSL frameworks
- Existing dependencies in the NextEntity project