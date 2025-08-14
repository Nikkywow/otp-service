package com.otp.dto;

import lombok.Data;

@Data
public class OTPValidationRequest {
    private String code;
    private String operationId;
}