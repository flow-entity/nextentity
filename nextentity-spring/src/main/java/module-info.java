open module nextentity.spring {
    requires java.sql;
    requires org.jspecify;
    requires spring.core;
    requires spring.jdbc;
    requires spring.tx;
    requires spring.context;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires nextentity.core;
    requires jakarta.persistence;
    requires java.naming;
    requires org.slf4j;
    requires spring.beans;

    exports io.github.nextentity.spring;
    exports io.github.nextentity.spring.event;
}