package com.otp.controller;

import com.otp.dto.AuthRequest;
import com.otp.dto.AuthResponse;
import com.otp.model.User;
import com.otp.service.AuthService;
import com.otp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        AuthResponse response = authService.authenticateUser(
                registeredUser.getUsername(),
                user.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.authenticateUser(
                authRequest.getUsername(),
                authRequest.getPassword());
        return ResponseEntity.ok(response);
    }
}