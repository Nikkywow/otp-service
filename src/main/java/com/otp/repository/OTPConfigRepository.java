package com.otp.repository;

import com.otp.model.OTPConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OTPConfigRepository {
    private final JdbcTemplate jdbcTemplate;

    public OTPConfigRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<OTPConfig> getConfig() {
        String sql = "SELECT * FROM otp_config LIMIT 1";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            OTPConfig config = new OTPConfig();
            config.setId(rs.getLong("id"));
            config.setCodeLength(rs.getInt("code_length"));
            config.setExpirationTime(rs.getInt("expiration_time"));
            return config;
        }).stream().findFirst();
    }

    public void updateConfig(OTPConfig config) {
        String sql = "UPDATE otp_config SET code_length = ?, expiration_time = ?";
        jdbcTemplate.update(sql, config.getCodeLength(), config.getExpirationTime());
    }

    public void initializeConfig() {
        if (getConfig().isEmpty()) {
            String sql = "INSERT INTO otp_config(code_length, expiration_time) VALUES(6, 300)";
            jdbcTemplate.update(sql);
        }
    }
}