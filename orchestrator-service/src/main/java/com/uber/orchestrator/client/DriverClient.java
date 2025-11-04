package com.uber.orchestrator.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class DriverClient {

    private final RestTemplate restTemplate;

    public DriverClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Long reserveNearest(double lat, double lng, Double radiusMeters) {
        String url = "http://localhost:8080/api/drivers/reserve";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("lat", lat);
        body.put("lng", lng);
        if (radiusMeters != null) body.put("radiusMeters", radiusMeters);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
        try {
            Map<?, ?> resp = restTemplate.postForObject(url, req, Map.class);
            if (resp == null || !resp.containsKey("driverId")) {
                return null;
            }
            Object driverId = resp.get("driverId");
            if (driverId instanceof Number n) {
                return n.longValue();
            }
            return Long.valueOf(String.valueOf(driverId));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 204) {
                return null; // no driver
            }
            throw e;
        }
    }
}
