package com.chc.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.chc.ai.mapper")
public class ToolsApp {
    public static void main(String[] args) {
        SpringApplication.run(ToolsApp.class, args);
    }
}
