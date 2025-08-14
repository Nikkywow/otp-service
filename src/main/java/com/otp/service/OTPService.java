package com.otp.service;

import com.otp.model.OTP;
import com.otp.model.OTP.Status;
import com.otp.model.User;
import com.otp.repository.OTPConfigRepository;
import com.otp.repository.OTPRepository;
import com.otp.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OTPService {
    private static final Logger logger = LoggerFactory.getLogger(OTPService.class);

    private final OTPRepository otpRepository;
    private final OTPConfigRepository configRepository;
    private final com.otp.service.NotificationService notificationService;
    private final MetricsService metricsService;

    public String generateOTP(String operationId, String channel, String destination) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        OTP otp = new OTP();
        otp.setUserId(user.getId());
        otp.setOperationId(operationId);
        otp.setStatus(Status.ACTIVE);
        otp.setCreatedAt(LocalDateTime.now());

        String code = CodeGenerator.generate(configRepository.getConfig().orElseThrow().getCodeLength());
        otp.setCode(code);

        otpRepository.save(otp);

        notificationService.sendNotification(channel, destination, code);

        logger.info("Generated OTP for user {} and operation {}", user.getId(), operationId);
        metricsService.incrementOTPGenerationCount();

        return code;
    }

    public boolean validateOTP(String code, String operationId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isValid = otpRepository.validate(user.getId(), code, operationId);

        if (isValid) {
            logger.info("Successful OTP validation for user {} and operation {}", user.getId(), operationId);
            metricsService.incrementOTPValidationSuccess();
        } else {
            logger.warn("Failed OTP validation attempt for user {} and operation {}", user.getId(), operationId);
            metricsService.incrementOTPValidationFailure();
        }

        return isValid;
    }

    @Scheduled(fixedRate = 60000) // Every minute
    public void checkExpiredCodes() {
        otpRepository.markExpiredCodes();
        logger.debug("Checked and marked expired OTP codes");
    }
}