package io.github.nextentity.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// SQL 日志记录工具类。
///
/// 提供统一的 SQL 日志记录功能，使用专用的日志记录器 "io.github.nextentity.sql"。
///
/// @author HuangChengwei
/// @since 1.0.0
public class SqlLogger {

    public static final Logger log = LoggerFactory.getLogger("io.github.nextentity.sql");

    public static void debug(String s) {
        log.debug(s);
    }

    public static void debug(String s, Object o) {
        log.debug(s, o);
    }

}
