package com.otp.controller;

import com.otp.dto.OTPConfigDTO;
import com.otp.model.OTPConfig;
import com.otp.model.User;
import com.otp.repository.OTPConfigRepository;
import com.otp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final OTPConfigRepository configRepository;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OTPConfig> getConfig() {
        return ResponseEntity.ok(configRepository.getConfig().orElseThrow());
    }

    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OTPConfig> updateConfig(@RequestBody OTPConfigDTO configDTO) {
        OTPConfig config = new OTPConfig();
        config.setCodeLength(configDTO.getCodeLength());
        config.setExpirationTime(configDTO.getExpirationTime());

        configRepository.updateConfig(config);
        return ResponseEntity.ok(configRepository.getConfig().orElseThrow());
    }
}