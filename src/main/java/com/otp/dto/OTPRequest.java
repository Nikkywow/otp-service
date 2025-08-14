package com.otp.dto;

import lombok.Data;

@Data
public class OTPRequest {
    private String channel;
    private String destination;
    private String operationId;
}