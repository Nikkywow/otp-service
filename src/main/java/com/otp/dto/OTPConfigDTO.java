package com.otp.dto;

import lombok.Data;

@Data
public class OTPConfigDTO {
    private Integer codeLength;
    private Integer expirationTime;
}