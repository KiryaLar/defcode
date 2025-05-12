package com.larkin.defcode.service;

import com.larkin.defcode.dao.TokenDao;
import com.larkin.defcode.dao.UserDao;
import com.larkin.defcode.dto.response.AuthenticationResponse;
import com.larkin.defcode.dto.request.LoginUserRequest;
import com.larkin.defcode.entity.RefreshToken;
import com.larkin.defcode.entity.TokenType;
import com.larkin.defcode.exception.InvalidJwtTokenException;
import com.larkin.defcode.security.UserDetailsImpl;
import com.larkin.defcode.security.service.JwtProvider;
import com.larkin.defcode.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserDao userDao;
    private final TokenDao tokenDao;


    public AuthenticationResponse authenticate(LoginUserRequest loginUserRequest) {
        log.debug("Authenticating user: {}", loginUserRequest);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserRequest.getUsername(),
                        loginUserRequest.getPassword()
                )
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return generateAndSaveTokens(userDetails);
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        log.debug("Refreshing tokens with refresh token: {}", refreshToken);
        jwtProvider.validateRefreshToken(refreshToken);
        setTokenRevoked(refreshToken);

        String login = jwtProvider.extractUsername(refreshToken, TokenType.REFRESH);
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(login);
        return generateAndSaveTokens(userDetails);
    }


    public void setTokenRevoked(String refreshToken) {
        log.debug("Setting token revoked: {}", refreshToken);
        Optional<RefreshToken> maybeToken = tokenDao.findRefreshToken(refreshToken);
        if (maybeToken.isEmpty()) {
            log.error("Refresh token {} wasn't found", refreshToken);
            throw new InvalidJwtTokenException("Invalid refresh token");
        }
        Integer tokenId = maybeToken.get().getId();
        tokenDao.revokeRefreshToken(tokenId);
        SecurityContextHolder.clearContext();
    }

    private AuthenticationResponse generateAndSaveTokens(UserDetailsImpl userDetails) {
        log.debug("Generating and storing tokens");
        String accessToken = jwtProvider.generateAccessToken(userDetails);
        String refreshToken = jwtProvider.generateRefreshToken(userDetails);

        Integer userId = userDetails.getUser().getId();
        Instant expiration = jwtProvider.extractExpiration(refreshToken);
        String role = jwtProvider.extractRole(accessToken, TokenType.ACCESS);

        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .token(refreshToken)
                .expirationDate(expiration)
                .build();

        tokenDao.saveRefreshToken(token);
        log.debug("Successfully generated tokens");
        return new AuthenticationResponse(role, accessToken, refreshToken);
    }
}
