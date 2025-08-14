package com.otp.service;

import com.otp.service.notification.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmailNotificationService emailService;
    private final SmsNotificationService smsService;
    private final TelegramNotificationService telegramService;
    private final FileNotificationService fileService;

    public void sendNotification(String channel, String destination, String code) {
        switch (channel.toUpperCase()) {
            case "EMAIL":
                emailService.sendCode(destination, code);
                break;
            case "SMS":
                smsService.sendCode(destination, code);
                break;
            case "TELEGRAM":
                telegramService.sendCode(destination, code);
                break;
            case "FILE":
                fileService.saveCode(code);
                break;
            default:
                throw new IllegalArgumentException("Unsupported notification channel: " + channel);
        }
    }
}