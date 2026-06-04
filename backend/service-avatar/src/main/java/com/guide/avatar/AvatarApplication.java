package com.guide.avatar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.guide")
public class AvatarApplication {
    public static void main(String[] args) {
        SpringApplication.run(AvatarApplication.class, args);
    }
}
