package com.larkin.defcode.service;

import com.larkin.defcode.dao.OperationDao;
import com.larkin.defcode.dao.OtpCodeDao;
import com.larkin.defcode.dao.OtpConfigDao;
import com.larkin.defcode.dao.UserDao;
import com.larkin.defcode.dto.request.GenerateOtpRequestDto;
import com.larkin.defcode.dto.request.ValidateOtpRequestDto;
import com.larkin.defcode.entity.OtpCode;
import com.larkin.defcode.exception.InvalidCredentialsException;
import com.larkin.defcode.exception.InvalidMethodToGenerateOtp;
import com.larkin.defcode.exception.InvalidOtpCodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpCodeService {

    private final OtpConfigDao otpConfigDao;
    private final OtpCodeDao otpCodeDao;
    private final UserDao userDao;
    private final OperationDao operationDao;
    private final EmailService emailService;
    private final SmsService smsService;
    private final TelegramService telegramService;

    public void sendOtp(GenerateOtpRequestDto request) {
        log.info("Sending otp code");
        int codeLength = otpConfigDao.findCodeLength();
        Duration lifetime = otpConfigDao.findLifetime();
        Instant expiration = Instant.now().plus(lifetime);
        int code = generateOtp(codeLength);
        String username = getCurrentUsername();
        int userId = userDao.findUserIdByUsername(username);
        Integer operationType = Integer.parseInt(request.getOperationType());
        operationDao.checkOperationType(operationType);

        otpCodeDao.saveOtpCode(code, userId, expiration, operationType);

        String method = request.getMethod();
        String contact = request.getContact();
        switch (method.toLowerCase()) {
            case "email" -> emailService.sendCode(contact, code);
            case "sms" -> smsService.sendCode(contact, code);
            case "telegram" -> telegramService.sendCode(contact, code);
            default -> {
                log.error("Invalid method to send code: {}", method);
                throw new InvalidMethodToGenerateOtp(method);
            }
        }
        log.debug("OTP code successfully sent");
    }

    public void validateOtp(ValidateOtpRequestDto request) {
        log.info("Validating otp code");
        Integer otpCode = Integer.parseInt(request.getCode());
        List<OtpCode> codes = otpCodeDao.findOtpCode(otpCode);

        String username = getCurrentUsername();
        int userId = userDao.findUserIdByUsername(username);

        Optional<OtpCode> maybeCode = codes.stream()
                .filter(code -> code.getUserId() == userId)
                .filter(code -> code.getStatus().equals("ACTIVE"))
                .findFirst();

        if (maybeCode.isEmpty()) {
            log.error("Invalid otp code: {}", otpCode);
            throw new InvalidOtpCodeException();
        }

        OtpCode code = maybeCode.get();
        Instant expirationTime = code.getExpirationTime();
        if (expirationTime.isBefore(Instant.now())) {
            log.error("OTP code is expired at: {}", expirationTime);
            otpCodeDao.setCodeAsExpired(code.getId());
            throw new InvalidOtpCodeException();
        }
    }

    private String getCurrentUsername() {
        log.debug("Getting current username from security context");
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidCredentialsException("User not authenticated");
        }
        return authentication.getName();
    }

    private int generateOtp(int codeLength) {
        log.info("Generating otp code");
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));
        }
        log.debug("Successfully generated otp code: {}", code);
        return Integer.parseInt(code.toString());
    }
}
