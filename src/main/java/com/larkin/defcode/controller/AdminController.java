package com.larkin.defcode.controller;

import com.larkin.defcode.dto.response.UserResponse;
import com.larkin.defcode.dto.request.OtpConfigRequest;
import com.larkin.defcode.dto.response.SuccessResponse;
import com.larkin.defcode.service.OtpCodeService;
import com.larkin.defcode.service.OtpConfigService;
import com.larkin.defcode.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;
    private final OtpConfigService otpConfigService;

    @PutMapping("/otp-config")
    public ResponseEntity<SuccessResponse> changeConfig(@Valid @RequestBody OtpConfigRequest request) {
        log.info("Change config request: {}", request);
        otpConfigService.changeOtpConfig(request);
        log.debug("Successfully changed config request");
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .message("OTP config was successfully changed: " +
                         "New duration: " + request.getLifetime() +
                         " New code length: " + request.getLength())
                .timestamp(new Date().toString())
                .build());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        log.info("Request for receiving users");
        return ResponseEntity.ok(userService.getNonAdminUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<SuccessResponse> deleteUser(@PathVariable Integer id) {
        log.info("Request for deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .message("User with id " + id + " deleted")
                .timestamp(new Date().toString())
                .build());
    }
}
