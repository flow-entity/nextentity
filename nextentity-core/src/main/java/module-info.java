open module nextentity.core {
    requires jakarta.persistence;
    requires java.desktop;
    requires java.sql;
    requires org.jspecify;
    requires org.slf4j;
    requires jdk.jlink;

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
    exports io.github.nextentity.core.reflect.schema.impl;
    exports io.github.nextentity.core.annotation;
    exports io.github.nextentity.core.meta.impl;
    exports io.github.nextentity.core.configuration;
    exports io.github.nextentity.jdbc.configuration;
    exports io.github.nextentity.jpa.configuration;
}