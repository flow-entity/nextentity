open module nextentity.spring {
    requires java.sql;
    requires org.jspecify;
    requires spring.core;
    requires spring.jdbc;
    requires spring.tx;
    requires nextentity.core;
    requires jakarta.persistence;
    requires java.naming;
    requires org.slf4j;

    exports io.github.nextentity.spring;
}