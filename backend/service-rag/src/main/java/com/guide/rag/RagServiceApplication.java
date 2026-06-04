package com.guide.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.guide")
public class RagServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RagServiceApplication.class, args);
    }
}
