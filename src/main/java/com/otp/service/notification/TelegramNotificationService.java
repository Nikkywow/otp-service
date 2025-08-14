package com.otp.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@PropertySource("classpath:application.properties")
public class TelegramNotificationService {

    private final String botToken;
    private final String chatId;
    private final boolean enabled;

    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot%s/sendMessage";
    private static final int MAX_MESSAGE_LENGTH = 4096;

    public TelegramNotificationService(
            @Value("${telegram.bot.token:#{null}}") String botToken,
            @Value("${telegram.chat.id:#{null}}") String chatId) {

        this.botToken = botToken;
        this.chatId = chatId;
        this.enabled = botToken != null && chatId != null;

        if (enabled) {
            log.info("Telegram notifications enabled for chat ID: {}", chatId);
        } else {
            log.warn("Telegram notifications disabled - missing configuration");
        }
    }

    public void sendCode(String destination, String code) {
        if (!enabled) {
            throw new IllegalStateException("Telegram notifications are not configured");
        }

        String message = String.format("Your confirmation code is: %s", truncateMessage(code));
        String url = buildTelegramUrl(message);

        try {
            sendTelegramRequest(url);
            log.info("Telegram message with OTP code sent to chat {}", chatId);
        } catch (Exception e) {
            log.error("Failed to send Telegram notification", e);
            throw new RuntimeException("Failed to send Telegram notification", e);
        }
    }

    private String truncateMessage(String code) {
        String message = "Your confirmation code is: " + code;
        if (message.length() > MAX_MESSAGE_LENGTH) {
            message = message.substring(0, MAX_MESSAGE_LENGTH);
            log.warn("Telegram message truncated to {} characters", MAX_MESSAGE_LENGTH);
        }
        return message;
    }

    private String buildTelegramUrl(String message) {
        return String.format(TELEGRAM_API_URL + "?chat_id=%s&text=%s",
                botToken,
                chatId,
                urlEncode(message));
    }

    private void sendTelegramRequest(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    String errorBody = new String(response.getEntity().getContent().readAllBytes());
                    log.error("Telegram API error. Status: {}. Response: {}",
                            response.getStatusLine().getStatusCode(), errorBody);
                    throw new RuntimeException("Telegram API error: " + errorBody);
                }
            }
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public boolean isEnabled() {
        return enabled;
    }
}