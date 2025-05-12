package com.larkin.defcode.controller;

import com.larkin.defcode.dto.request.GenerateOtpRequestDto;
import com.larkin.defcode.dto.request.ValidateOtpRequestDto;
import com.larkin.defcode.dto.response.SuccessResponse;
import com.larkin.defcode.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final OtpCodeService otpCodeService;

    @PostMapping("/otp/generate")
    public ResponseEntity<SuccessResponse> generateOtpCode(@Valid @RequestBody GenerateOtpRequestDto request) {
        log.info("Request to generate otp code: {}", request);
        otpCodeService.sendOtp(request);
        return ResponseEntity.ok()
                .body(SuccessResponse.builder()
                        .timestamp(new Date().toString())
                        .message("Code was successfully sent")
                        .build());
    }

    @PostMapping("/otp/validate")
    public ResponseEntity<SuccessResponse> validateOtpCode(@Valid @RequestBody ValidateOtpRequestDto request) {
        log.info("Request to validate otp code: {}", request);
        otpCodeService.validateOtp(request);
        return ResponseEntity.ok()
                .body(SuccessResponse.builder()
                        .timestamp(new Date().toString())
                        .message("Code is correct")
                        .build());
    }
}
