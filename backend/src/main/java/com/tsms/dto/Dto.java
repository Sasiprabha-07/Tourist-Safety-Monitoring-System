package com.tsms.dto;

import lombok.*;

/**
 * Data Transfer Objects (DTOs) for TSMS
 * These are simple Java classes used to carry data between layers
 */
public class Dto {

    // ---- Login Request ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    // ---- Login Response ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginResponse {
        private boolean success;
        private String message;
        private String username;
    }

    // ---- Register Request ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String fullName;
        private String username;
        private String password;
    }

    // ---- Tourist Request (Add/Update) ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TouristRequest {
        private String name;
        private String location;
        private String status;
    }

    // ---- Generic API Response ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
    }
}
