package com.larkin.defcode.controller;

import com.larkin.defcode.dto.request.RefreshTokenRequest;
import com.larkin.defcode.dto.response.AuthenticationResponse;
import com.larkin.defcode.dto.request.LoginUserRequest;
import com.larkin.defcode.dto.request.RegisterUserRequest;
import com.larkin.defcode.dto.response.SuccessResponse;
import com.larkin.defcode.service.AuthService;
import com.larkin.defcode.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginUserRequest user) {
        log.info("Request for user login: {}", user);
        return ResponseEntity.ok(authService.authenticate(user));
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> register(@Valid @RequestBody RegisterUserRequest userDto) {
        log.info("Request for user register: {}", userDto);
        userService.registerUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.builder()
                        .timestamp(new Date().toString())
                        .message("User " + userDto.getUsername() + " has successfully registered as an " + userDto.getRole())
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Request for user logout: {}", request);
        String refreshToken = request.getRefreshToken();
        authService.setTokenRevoked(refreshToken);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.builder()
                        .timestamp(new Date().toString())
                        .message("Success logged out")
                        .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Request for refresh tokens: {}", request);
        return ResponseEntity
                .ok()
                .body(authService.refreshToken(request.getRefreshToken()));
    }
}
