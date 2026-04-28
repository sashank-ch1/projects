package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.context.annotation.Bean;
@Configuration
public class WhatsAppConfig {

    @Value("${whatsapp.access-token}")
    private String accessToken;
    
    @Bean
    public WebClient whatsAppWebClient(@Value("${whatsapp.api.url}") String baseUrl) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("WhatsApp access token is missing!");
        }

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
    
    

