package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WhatsAppService {

    private final WebClient webClient;
    
    private static final Logger log = LoggerFactory.getLogger(WhatsAppService.class);
    
    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId;

    // The Bean name 'whatsAppWebClient' in Config matches this parameter name
    public WhatsAppService(WebClient whatsAppWebClient) {
        this.webClient = whatsAppWebClient;
    }

    // ✅ Send a plain text message
    public String sendTextMessage(String toPhone, String messageText) {
        log.info("Sending text message to: {}", toPhone);
        
        Map<String, Object> body = Map.of(
            "messaging_product", "whatsapp",
            "recipient_type", "individual",
            "to", toPhone,
            "type", "text",
            "text", Map.of("body", messageText)
        );

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/{phoneId}/messages").build(phoneNumberId))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block(); 
    }

    // ✅ Send a template message (like OTP)
    public String sendTemplateMessage(String toPhone, String templateName, String langCode, String otpValue) {
        log.info("Sending template: {} to: {}", templateName, toPhone);

        Map<String, Object> body = Map.of(
            "messaging_product", "whatsapp",
            "to", toPhone,
            "type", "template",
            "template", Map.of(
                "name", templateName,
                "language", Map.of("code", langCode),
                "components", List.of(Map.of(
                    "type", "body",
                    "parameters", List.of(Map.of(
                        "type", "text",
                        "text", otpValue
                    ))
                ))
            )
        );

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/{phoneId}/messages").build(phoneNumberId))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}