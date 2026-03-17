package com.tsms.service;

import com.tsms.dto.Dto;
import com.tsms.model.User;
import com.tsms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * AuthService — handles login and registration logic
 * Uses simple plain-text password matching (suitable for college mini project)
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    // ---- Login ----
    public Dto.LoginResponse login(Dto.LoginRequest req) {
        // Find user by username
        return userRepository.findByUsername(req.getUsername())
                .map(user -> {
                    // Check password match
                    if (user.getPassword().equals(req.getPassword())) {
                        return Dto.LoginResponse.builder()
                                .success(true)
                                .message("Login successful")
                                .username(user.getUsername())
                                .build();
                    } else {
                        return Dto.LoginResponse.builder()
                                .success(false)
                                .message("Invalid password")
                                .build();
                    }
                })
                .orElse(Dto.LoginResponse.builder()
                        .success(false)
                        .message("User not found")
                        .build());
    }

    // ---- Register ----
    public Dto.ApiResponse register(Dto.RegisterRequest req) {
        // Check if username already exists
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            return Dto.ApiResponse.builder()
                    .success(false)
                    .message("Username already exists")
                    .build();
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(req.getPassword())   // plain text for simplicity
                .fullName(req.getFullName())
                .build();

        userRepository.save(user);

        return Dto.ApiResponse.builder()
                .success(true)
                .message("Registration successful")
                .build();
    }
}
