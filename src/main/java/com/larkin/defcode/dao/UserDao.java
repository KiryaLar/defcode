package com.larkin.defcode.dao;

import com.larkin.defcode.entity.Role;
import com.larkin.defcode.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    public void createUser(User user) {
        log.info("Creating user: {}", user);
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, CAST(? AS role))";
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getRole().name());
        log.debug("User successfully created");
    }

    public List<User> findByRole(String role) {
        log.info("Finding users by role: {}", role);
        String sql = "SELECT id, username, password, role FROM users WHERE role = ?::role";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs), role);
    }

    public Optional<User> findByUsername(String username) {
        log.info("User search by username: {}", username);
        String sql = "SELECT id, username, password, role FROM users WHERE username = ? ";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs), username);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    public void deleteById(Integer id) {
        log.info("Deleting user with ID: {}", id);
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
        log.debug("User successfully deleted");
    }

    public Integer findUserIdByUsername(String username) {
        log.debug("User ID search by username: {}", username);
        String sql = "SELECT id FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, username);
    }

    private User mapUser(ResultSet rs) throws SQLException {
        log.debug("Getting user from ResultSet");
        return User.builder()
                .id(rs.getInt("id"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .role(Role.valueOf(rs.getString("role")))
                .build();
    }
}
