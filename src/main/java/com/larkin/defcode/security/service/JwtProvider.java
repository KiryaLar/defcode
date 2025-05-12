package com.larkin.defcode.security.service;

import com.larkin.defcode.dao.TokenDao;
import com.larkin.defcode.entity.RefreshToken;
import com.larkin.defcode.entity.TokenType;
import com.larkin.defcode.exception.InvalidJwtTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    private final int expirationTimeAccessInMinutes;
    private final int expirationTimeRefreshInDays;
    private final TokenDao tokenDao;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret,
            @Value("${jwt.expiration-time.access}") int expirationTimeAccessInMinutes,
            @Value("${jwt.expiration-time.refresh}") int expirationTimeRefreshInDays,
            TokenDao tokenDao) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
        this.expirationTimeAccessInMinutes = expirationTimeAccessInMinutes;
        this.expirationTimeRefreshInDays = expirationTimeRefreshInDays;
        this.tokenDao = tokenDao;
    }

    public String generateAccessToken(UserDetails userDetails) {
        log.debug("Generating access token");
        Date accessExpiration = Date.from(
                LocalDateTime.now()
                        .plusMinutes(expirationTimeAccessInMinutes)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .claim("role", role)
                .expiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        log.debug("Generating refresh token");
        Date refreshExpiration = Date.from(
                LocalDateTime.now()
                        .plusDays(expirationTimeRefreshInDays)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public Claims extractAccessClaims(String token) {
        log.debug("Extracting from access token");
        return extractClaims(token, jwtAccessSecret);
    }

    public Claims extractRefreshClaims(String token) {
        log.debug("Extracting from refresh token");
        return extractClaims(token, jwtRefreshSecret);
    }

    public String extractUsername(String token, TokenType tokenType) {
        log.debug("Extracting username from token {}", token);
        if (tokenType.equals(TokenType.ACCESS)) {
            Claims claims = extractAccessClaims(token);
            return claims.getSubject();
        } else if (tokenType.equals(TokenType.REFRESH)) {
            Claims claims = extractRefreshClaims(token);
            return claims.getSubject();
        } else {
            log.error("Invalid token type");
            throw new InvalidJwtTokenException("Invalid token type");
        }
    }

    public String extractRole(String token, TokenType tokenType) {
        log.debug("Extracting role");
        if (tokenType.equals(TokenType.ACCESS)) {
            Claims claims = extractAccessClaims(token);
            return claims.get("role", String.class);
        } else if (tokenType.equals(TokenType.REFRESH)) {
            Claims claims = extractRefreshClaims(token);
            return claims.get("role", String.class);
        } else {
            log.error("Invalid token type");
            throw new InvalidJwtTokenException("Invalid token type");
        }
    }

    public Instant extractExpiration(String refreshToken) {
        log.debug("Extracting expiration time");
        return extractRefreshClaims(refreshToken).getExpiration().toInstant();
    }

    private Claims extractClaims(String token, SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public void validateAccessToken(@NotNull String accessToken) {
        log.debug("Validating access token");
        validateToken(accessToken, jwtAccessSecret, TokenType.ACCESS);
    }

    public void validateRefreshToken(@NotNull String refreshToken) {
        log.debug("Validating refresh token");
        Optional<RefreshToken> token = tokenDao.findRefreshToken(refreshToken);
        if (token.isEmpty()) {
            log.error("Refresh token {} not found", refreshToken);
            throw new InvalidJwtTokenException("Invalid Refresh token");
        }
        if (token.get().isRevoked()) {
            log.error("Refresh token {} is revoked", refreshToken);
            throw new InvalidJwtTokenException("Refresh token is revoked");
        }
        validateToken(refreshToken, jwtRefreshSecret, TokenType.REFRESH);
    }

    private void validateToken(@NotNull String token, @NotNull SecretKey secret, TokenType tokenType) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
            throw new InvalidJwtTokenException(tokenType.getValue() + " token expired");
        } catch (UnsupportedJwtException | SignatureException unsEx) {
            log.error("Unsupported jwt", unsEx);
            throw new InvalidJwtTokenException("Invalid " + tokenType.getValue() + " token signature");
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
            throw new InvalidJwtTokenException("Malformed " + tokenType.getValue() + " token");
        } catch (Exception e) {
            log.error("Invalid token", e);
            throw new InvalidJwtTokenException("Invalid " + tokenType.getValue() + " token");
        }
    }
}
