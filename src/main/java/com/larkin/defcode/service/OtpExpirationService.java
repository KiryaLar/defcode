package com.larkin.defcode.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpExpirationService {
    private final JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 300000)
    public void expireOldOtps() {
        log.info("Expiring old OTP codes");
        String sql = "UPDATE otp_codes SET status = 'EXPIRED' WHERE status = 'ACTIVE' AND expiration_time < NOW()";
        jdbcTemplate.update(sql);
    }
}
