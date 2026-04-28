package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
 // From Spring Boot
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import java.util.HashMap;
import java.util.Map;

@Service
public class ChatigoService {

    private static final String BASE_URL = "https://api.chatigo.in/v1";

    @Value("${chatigo.api-key}")
    private String apiKey;

    @Value("${chatigo.phone-number}")
    private String fromNumber;

    private final RestTemplate restTemplate;

    // RestTemplateBuilder is provided by Spring Boot automatically
    public ChatigoService() {
        this.restTemplate = new RestTemplate();
    }

    public String sendMessage(String toPhone, String message) {
        // Create Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare Request Body
        Map<String, Object> body = new HashMap<>();
        body.put("from", fromNumber);
        body.put("to", toPhone);
        body.put("type", "text");
        body.put("text", Map.of("body", message));

        // Wrap into HttpEntity
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // Execute POST Request
        ResponseEntity<String> response = restTemplate.postForEntity(
            BASE_URL + "/messages", entity, String.class);

        return response.getBody();
    }
}
