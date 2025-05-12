package com.larkin.defcode.dao;

import com.larkin.defcode.entity.OtpCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OtpCodeDao {

    private final JdbcTemplate jdbcTemplate;

    public void saveOtpCode(int otpCode, int userId, Instant expirationTime, Integer operationType) {
        log.info("Saving OTP Code {} to DB", otpCode);
        String sql = "INSERT INTO otp_codes (code, user_id, expiration_time, operation_type) VALUES (?,?,?,?)";
        jdbcTemplate.update(sql, otpCode, userId, Timestamp.from(expirationTime), operationType);
        log.debug("Successfully saved OTP Code {} to DB", otpCode);
    }

    public List<OtpCode> findOtpCode(Integer code) {
        log.info("Finding OTP Code: {}", code);
        String sql = "SELECT id, code, user_id, status, expiration_time, operation_type FROM otp_codes WHERE code=?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapOtpCode(rs), code);
    }

    public void setCodeAsExpired(Integer id) {
        log.debug("Setting code as expired at db");
        String sql = "UPDATE otp_codes SET status='EXPIRED' WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

    private OtpCode mapOtpCode(ResultSet rs) throws SQLException {
        log.debug("Getting OTP Code from ResultSet");
        return OtpCode.builder()
                .id(rs.getInt("id"))
                .code(rs.getInt("code"))
                .userId(rs.getInt("user_id"))
                .expirationTime(rs.getTimestamp("expiration_time").toInstant())
                .status(rs.getString("status"))
                .operationType(rs.getInt("operation_type"))
                .build();
    }
}
