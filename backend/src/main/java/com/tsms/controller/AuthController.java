package com.tsms.controller;

import com.tsms.dto.Dto;
import com.tsms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController — REST API endpoints for login and registration
 *
 * POST /api/auth/login    → Validate username & password
 * POST /api/auth/register → Create new user account
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")   // Allow frontend to call from any origin
public class AuthController {

    private final AuthService authService;

    // ---- LOGIN ----
    @PostMapping("/login")
    public ResponseEntity<Dto.LoginResponse> login(@RequestBody Dto.LoginRequest request) {
        Dto.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // ---- REGISTER ----
    @PostMapping("/register")
    public ResponseEntity<Dto.ApiResponse> register(@RequestBody Dto.RegisterRequest request) {
        Dto.ApiResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
}
