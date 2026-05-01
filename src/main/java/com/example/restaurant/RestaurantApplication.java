package com.example.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestaurantApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
        System.out.println("========================================");
        System.out.println("=== RESTAURANT APP STARTED SUCCESSFULLY ===");
        System.out.println("=== Access at: http://localhost:8080 ===");
        System.out.println("========================================");
    }
}