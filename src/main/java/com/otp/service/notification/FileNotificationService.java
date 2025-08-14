package com.otp.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class FileNotificationService {
    private static final String FILE_PATH = "otp_codes.txt";

    public void saveCode(String code) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(String.format("[%s] OTP Code: %s%n", LocalDateTime.now(), code));
            log.info("OTP code saved to file {}", FILE_PATH);
        } catch (IOException e) {
            log.error("Failed to save OTP code to file: {}", e.getMessage());
            throw new RuntimeException("Failed to save OTP code to file", e);
        }
    }
}