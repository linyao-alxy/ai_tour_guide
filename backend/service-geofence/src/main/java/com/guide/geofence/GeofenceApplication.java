package com.guide.geofence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.guide")
public class GeofenceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeofenceApplication.class, args);
    }
}
