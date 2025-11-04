package com.uber.backend.health;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "ok");
        resp.put("timestamp", Instant.now().toString());
        return resp;
    }

    @GetMapping("/db-health")
    public Map<String, Object> dbHealth() {
        Map<String, Object> resp = new HashMap<>();
        try {
            Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            resp.put("db", one != null && one == 1 ? "ok" : "unexpected");
        } catch (Exception e) {
            resp.put("db", "error");
            resp.put("error", e.getMessage());
        }
        resp.put("timestamp", Instant.now().toString());
        return resp;
    }
}
