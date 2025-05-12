package com.larkin.defcode.dao;

import com.larkin.defcode.exception.NotFoundException;
import com.larkin.defcode.util.DurationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGInterval;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OtpConfigDao {

    private final JdbcTemplate jdbcTemplate;

    public void updateOtpConfig(Duration lifetime, int codeLength) {
        log.info("Updating OTP config.");
        log.info("lifetime: {}, code length: {}", lifetime, codeLength);
        String sql = "INSERT INTO otp_config (id, lifetime, code_length) VALUES (1, ?, ?) " +
                     "ON CONFLICT (id) DO UPDATE SET lifetime = ?, code_length = ?";
        jdbcTemplate.update(sql, DurationUtil.durationToPgInterval(lifetime), codeLength, DurationUtil.durationToPgInterval(lifetime), codeLength);
        log.info("Successfully updated OTP config");
    }

    public int findCodeLength() {
        log.debug("Finding code length");
        String sql = "SELECT code_length FROM otp_config";
        List<Integer> codeLength = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("code_length"));
        if (codeLength.isEmpty()) {
            log.error("OTP config not found");
            NotFoundException.otpConfig();
        }
        log.debug("Code length: {}", codeLength);
        return codeLength.getFirst();
    }

    public Duration findLifetime() {
        log.debug("Finding lifetime");
        String sql = "SELECT lifetime FROM otp_config";
        Duration duration = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            PGInterval pgInterval = (PGInterval) rs.getObject("lifetime");
            return DurationUtil.pgIntervalToDuration(pgInterval);
        });
        log.debug("Lifetime duration in seconds: {}", duration != null ? duration.getSeconds() : 0);
        return duration;
    }
}
