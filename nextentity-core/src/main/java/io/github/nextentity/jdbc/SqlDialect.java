package io.github.nextentity.jdbc;

/// SQL dialect interface for database-specific SQL syntax.
///
/// Provides methods for database-specific identifier quoting and other
/// SQL syntax variations.
///
/// @author HuangChengwei
/// @since 2.1
public interface SqlDialect {

    /// Returns the left quote character for identifiers.
    ///
    /// @return Left quote character (e.g., "`" for MySQL, "\"" for standard SQL)
    String leftQuotedIdentifier();

    /// Returns the right quote character for identifiers.
    ///
    /// @return Right quote character (e.g., "`" for MySQL, "\"" for standard SQL)
    String rightQuotedIdentifier();

    /// MySQL SQL dialect implementation.
    SqlDialect MYSQL = new SqlDialect() {
        @Override
        public String leftQuotedIdentifier() {
            return "`";
        }

        @Override
        public String rightQuotedIdentifier() {
            return "`";
        }
    };

    /// PostgreSQL SQL dialect implementation.
    SqlDialect POSTGRESQL = new SqlDialect() {
        @Override
        public String leftQuotedIdentifier() {
            return "\"";
        }

        @Override
        public String rightQuotedIdentifier() {
            return "\"";
        }
    };

    /// SQL Server SQL dialect implementation.
    SqlDialect SQL_SERVER = new SqlDialect() {
        @Override
        public String leftQuotedIdentifier() {
            return "[";
        }

        @Override
        public String rightQuotedIdentifier() {
            return "]";
        }
    };
}