package com.otp.repository;

import com.otp.model.OTP;
import com.otp.model.OTP.Status;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OTPRepository {
    private final JdbcTemplate jdbcTemplate;

    public OTPRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(OTP otp) {
        String sql = "INSERT INTO otp_codes(user_id, code, status, operation_id) VALUES(?, ?, ?::otp_status, ?)";
        jdbcTemplate.update(sql,
                otp.getUserId(),
                otp.getCode(),
                otp.getStatus().name(),
                otp.getOperationId());
    }

    public boolean validate(Long userId, String code, String operationId) {
        String sql = "UPDATE otp_codes SET status = 'USED' " +
                "WHERE user_id = ? AND operation_id = ? AND code = ? AND status = 'ACTIVE' " +
                "RETURNING id";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getLong("id"),
                userId, operationId, code).size() > 0;
    }

    public void markExpiredCodes() {
        String sql = "UPDATE otp_codes SET status = 'EXPIRED' " +
                "WHERE status = 'ACTIVE' AND created_at < NOW() - INTERVAL '1 second' * " +
                "(SELECT expiration_time FROM otp_config LIMIT 1)";
        jdbcTemplate.update(sql);
    }

    public List<OTP> findAllByUserId(Long userId) {
        String sql = "SELECT * FROM otp_codes WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            OTP otp = new OTP();
            otp.setId(rs.getLong("id"));
            otp.setUserId(rs.getLong("user_id"));
            otp.setCode(rs.getString("code"));
            otp.setStatus(Status.valueOf(rs.getString("status")));
            otp.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            otp.setOperationId(rs.getString("operation_id"));
            return otp;
        }, userId);
    }
}