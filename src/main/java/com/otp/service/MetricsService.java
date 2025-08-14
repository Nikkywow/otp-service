package com.otp.service;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsService {
    private final Counter otpGenerationCounter;
    private final Counter otpValidationSuccessCounter;
    private final Counter otpValidationFailureCounter;
    private final Timer otpGenerationTimer;
    private final Timer otpValidationTimer;
    private final AtomicInteger activeSessionsGauge;

    public MetricsService(MeterRegistry registry) {
        // Инициализация счетчиков
        this.otpGenerationCounter = Counter.builder("otp.generation.count")
                .description("Total number of OTP codes generated")
                .register(registry);

        this.otpValidationSuccessCounter = Counter.builder("otp.validation.success.count")
                .description("Total number of successful OTP validations")
                .register(registry);

        this.otpValidationFailureCounter = Counter.builder("otp.validation.failure.count")
                .description("Total number of failed OTP validations")
                .register(registry);

        // Инициализация таймеров
        this.otpGenerationTimer = Timer.builder("otp.generation.time")
                .description("Time taken to generate OTP codes")
                .register(registry);

        this.otpValidationTimer = Timer.builder("otp.validation.time")
                .description("Time taken to validate OTP codes")
                .register(registry);

        // Инициализация gauge для активных сессий
        this.activeSessionsGauge = registry.gauge("otp.active.sessions", new AtomicInteger(0));
    }

    // Методы для работы с метриками
    public void incrementOTPGenerationCount() {
        otpGenerationCounter.increment();
    }

    public void incrementOTPValidationSuccess() {
        otpValidationSuccessCounter.increment();
    }

    public void incrementOTPValidationFailure() {
        otpValidationFailureCounter.increment();
    }

    public Timer.Sample startGenerationTimer() {
        return Timer.start();
    }

    public void stopGenerationTimer(Timer.Sample sample) {
        sample.stop(otpGenerationTimer);
    }

    public Timer.Sample startValidationTimer() {
        return Timer.start();
    }

    public void stopValidationTimer(Timer.Sample sample) {
        sample.stop(otpValidationTimer);
    }

    public void setActiveSessionsCount(int count) {
        activeSessionsGauge.set(count);
    }
}