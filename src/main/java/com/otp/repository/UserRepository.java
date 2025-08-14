package com.otp.repository;

import com.otp.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    // Маппер для преобразования ResultSet в объект User
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        return user;
    };

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Реализация findById
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Ваш существующий метод findByUsername
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, username);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) > 0 FROM users WHERE username = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, username));
    }

    public boolean existsAdmin() {
        String sql = "SELECT COUNT(*) > 0 FROM users WHERE role = 'ADMIN'";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class));
    }


    public User save(User user) {
        if (user.getId() == null) {
            // Insert new user
            String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, ?) RETURNING id";
            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole().name());
            user.setId(id);
        } else {
            // Update existing user
            String sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole().name(),
                    user.getId());
        }
        return user;
    }

    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users WHERE role = 'USER'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(User.Role.valueOf(rs.getString("role")));
            return user;
        });
    }

    public void delete(Long userId) {
        jdbcTemplate.update("DELETE FROM otp_codes WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
    }
}