package com.otp.controller;

import com.otp.dto.OTPRequest;
import com.otp.dto.OTPValidationRequest;
import com.otp.service.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OTPController {
    private final OTPService otpService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> generateOTP(@RequestBody OTPRequest request) {
        String code = otpService.generateOTP(
                request.getOperationId(),
                request.getChannel(),
                request.getDestination());
        return ResponseEntity.ok(code);
    }

    @PostMapping("/validate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> validateOTP(@RequestBody OTPValidationRequest request) {
        boolean isValid = otpService.validateOTP(request.getCode(), request.getOperationId());
        return ResponseEntity.ok(isValid);
    }
}