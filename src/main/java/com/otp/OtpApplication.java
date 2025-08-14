package com.otp;

import com.otp.model.User;
import com.otp.repository.OTPConfigRepository;
import com.otp.repository.UserRepository;
import com.otp.service.UserService;
import com.otp.util.AppConstants;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OtpApplication {
    public static void main(String[] args) {
        SpringApplication.run(OtpApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(OTPConfigRepository configRepository,
                                  UserService userService,
                                  UserRepository userRepository) {
        return args -> {
            // Инициализация конфигурации OTP
            configRepository.initializeConfig();

            // Создание администратора по умолчанию, если его нет
            if (!userRepository.existsAdmin()) {
                User admin = new User();
                admin.setUsername(AppConstants.DEFAULT_ADMIN_USERNAME);
                admin.setPassword(AppConstants.DEFAULT_ADMIN_PASSWORD);
                admin.setRole(User.Role.ADMIN);

                userService.register(admin);
            }
        };
    }
}