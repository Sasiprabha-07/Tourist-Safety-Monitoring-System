package com.tsms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TsmsApplication — Main entry point for Tourist Safety Management System
 * Run this class to start the Spring Boot server on port 8080
 */
@SpringBootApplication
public class TsmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TsmsApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  TSMS Backend started at port 8080");
        System.out.println("  Open: http://localhost:8080/api");
        System.out.println("========================================\n");
    }
}
