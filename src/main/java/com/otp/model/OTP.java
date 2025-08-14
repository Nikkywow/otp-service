package com.otp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OTP {
    private Long id;
    private Long userId;
    private String code;
    private Status status;
    private LocalDateTime createdAt;
    private String operationId;

    public enum Status {
        ACTIVE, EXPIRED, USED
    }
}