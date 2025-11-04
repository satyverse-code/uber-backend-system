package com.uber.orchestrator.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class BookingClient {

    private final RestTemplate restTemplate;

    public BookingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Long createRide(Long riderId, String source, String destination) {
        String url = "http://localhost:8080/api/rides";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("riderId", riderId);
        body.put("source", source);
        body.put("destination", destination);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
        Map<?, ?> resp = restTemplate.postForObject(url, req, Map.class);
        if (resp == null || !resp.containsKey("rideId")) {
            throw new IllegalStateException("Ride creation failed");
        }
        Object rideId = resp.get("rideId");
        if (rideId instanceof Number n) {
            return n.longValue();
        }
        return Long.valueOf(String.valueOf(rideId));
    }
}
