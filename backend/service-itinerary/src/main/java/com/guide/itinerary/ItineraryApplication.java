package com.guide.itinerary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.guide")
public class ItineraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItineraryApplication.class, args);
    }
}
