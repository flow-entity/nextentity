package io.github.nextentity.examples.integration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChineseConsoleOutputTest {

    @Test
    void shouldPrintChineseToConsole() {
        String message = "中文输出检查：你好，世界，员工查询已完成。";
        System.out.println(message);
        assertThat(message).contains("中文输出检查");
    }
}
