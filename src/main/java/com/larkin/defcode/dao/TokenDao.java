package com.larkin.defcode.dao;

import com.larkin.defcode.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TokenDao {
    private final JdbcTemplate jdbcTemplate;

    public Optional<RefreshToken> findRefreshToken(String refreshToken) {
        log.info("Refresh token search: {}", refreshToken);
        String sql = "SELECT id, user_id, token, expiration_date, revoked FROM token WHERE token = ?";
        RefreshToken token = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapToken(rs), refreshToken);
        return Optional.ofNullable(token);
    }

    public void saveRefreshToken(RefreshToken token) {
        log.info("Saving refresh token:");
        log.info("INSERT INTO token (user_id, token, expiration_date) VALUES ({}, {}, {})", token.getUserId(), token.getToken(), Date.from(token.getExpirationDate()));
        String sql = "INSERT INTO token (user_id, token, expiration_date) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, token.getUserId(), token.getToken(), Date.from(token.getExpirationDate()));
        log.debug("Successfully saved refresh token");
    }

    public void revokeRefreshToken(Integer tokenId) {
        log.info("Revoking refresh token with ID: {}", tokenId);
        String sql = "UPDATE token SET revoked = true WHERE id = ?";
        jdbcTemplate.update(sql, tokenId);
        log.debug("Successfully revoked refresh token with ID: {}", tokenId);
    }

    private RefreshToken mapToken(ResultSet rs) throws SQLException {
        log.debug("Getting Refresh Token from ResultSet");
        return RefreshToken.builder()
                .id(rs.getInt("id"))
                .userId(rs.getInt("user_id"))
                .token(rs.getString("token"))
                .expirationDate((rs.getTimestamp("expiration_date")).toInstant())
                .revoked(rs.getBoolean("revoked"))
                .build();
    }
}
