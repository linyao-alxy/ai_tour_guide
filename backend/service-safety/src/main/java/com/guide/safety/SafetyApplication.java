package com.guide.safety;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.guide")
public class SafetyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SafetyApplication.class, args);
    }
}
