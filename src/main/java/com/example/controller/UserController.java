package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // SQL Injection Fix: Using parameterized queries
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        // BEFORE (vulnerable): "SELECT * FROM users WHERE id = " + id
        // AFTER (secure): Using parameterized query
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserRowMapper());
    }
    
    // SQL Injection Fix: Using parameterized queries for search
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String name) {
        // BEFORE (vulnerable): "SELECT * FROM users WHERE name LIKE '%" + name + "%'"
        // AFTER (secure): Using parameterized query with proper escaping
        String sql = "SELECT * FROM users WHERE name LIKE ?";
        String searchPattern = "%" + name.replaceAll("[%_]", "\\$0") + "%";
        return jdbcTemplate.query(sql, new Object[]{searchPattern}, new UserRowMapper());
    }
    
    // SQL Injection Fix: Input validation and parameterized query
    @PostMapping
    public void createUser(@RequestBody User user) {
        // Input validation
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Parameterized query for insert
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        jdbcTemplate.update(sql, user.getName(), user.getEmail());
    }
    
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            return user;
        }
    }
}