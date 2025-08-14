package com.otp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OTPConfig {
    private Long id;
    private Integer codeLength = 6;
    private Integer expirationTime = 300; // 5 minutes in seconds
}