open module nextentity.core {
    requires jakarta.persistence;
    requires java.desktop;
    requires java.sql;
    requires org.jspecify;
    requires org.slf4j;

    exports io.github.nextentity.api;
    exports io.github.nextentity.core.expression;
    exports io.github.nextentity.core;
    exports io.github.nextentity.core.util;
    exports io.github.nextentity.core.exception;
    exports io.github.nextentity.jdbc;
    exports io.github.nextentity.meta.jpa;
    exports io.github.nextentity.core.meta;
    exports io.github.nextentity.jpa;
    exports io.github.nextentity.api.model;
    exports io.github.nextentity.core.reflect.schema;
    exports io.github.nextentity.core.reflect;
    exports io.github.nextentity.core.annotation;
}