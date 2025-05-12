package com.larkin.defcode.service;

import com.larkin.defcode.exception.CodeSendingMethodException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendCode(String email, int code) {
        try {
            log.info("Sending code by email");
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Your OTP Code");
            message.setText("Your verification code is " + code);
            message.setFrom("kerchiklar@yandex.ru");
            mailSender.send(message);
            log.info("Successfully sent OTP Code");
        } catch (Exception e) {
            log.error("Couldn't send OTP Code by email: {}", e.getMessage());
            CodeSendingMethodException.email("Failed to send code to " + email);
        }
    }
}
