package com.guide.aiproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.guide")
public class AiProxyApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiProxyApplication.class, args);
    }
}
