package com.fleetforge.driver.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class AuthServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${internal.service.key}")
    private String internalKey;

    private final String AUTH_URL = "http://auth-service:8081/api/auth/create-driver-user";

    public String createDriverUser(String username) {

        Map<String, String> body = Map.of("username", username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("X-Internal-Key", internalKey);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        Map<String, Object> response =
                restTemplate.postForObject(AUTH_URL, entity, Map.class);

        return (String) response.get("tempPassword");
    }
}
