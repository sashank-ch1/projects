package com.example.demo.service;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatigoService {
	@Value("${chatigo.api.base-url}") private String baseUrl;
    @Value("${chatigo.api-key}")      private String apiKey;
    @Value("${chatigo.from-number}")  private String fromNumber;

    private final RestTemplate restTemplate;
    
    private static final Logger log = LoggerFactory.getLogger(ChatigoService.class);
    
    public ChatigoService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    // ────────────────────────────────────────────────────────
    // Send a plain text message (within 24h session window)
    // ────────────────────────────────────────────────────────
    public ChatigoResponse sendText(String toPhone, String message) {
    	Map<String, Object> body = buildBase(toPhone, "text");
        body.put("text", Map.of("body", message));
        return call(body);
    }

    // ────────────────────────────────────────────────────────
    // Send an approved template (business-initiated messages)
    // ────────────────────────────────────────────────────────
    public ChatigoResponse sendTemplate(String toPhone, String templateName,
                                        List<String> params) {
        List<Map<String,Object>> components = List.of(
            Map.of("type", "body",
                   "parameters", params.stream()
                       .map(p -> Map.of("type","text","text",p))
                       .collect(Collectors.toList()))
        );
        Map<String, Object> body = buildBase(toPhone, "template");
        body.put("template", Map.of(
            "name",       templateName,
            "language",   Map.of("code", "en"),
            "components", components
        ));
        return call(body);
    }

    // ────────────────────────────────────────────────────────
    // Send interactive buttons (e.g., Accept Load / Decline)
    // ────────────────────────────────────────────────────────
    public ChatigoResponse sendButtons(String toPhone, String bodyText,
                                        String header,
                                        List<String[]> buttons) {
        // buttons: List of [id, title] pairs
        List<Map<String,Object>> btnList = buttons.stream()
            .map(b -> Map.of("type","reply","reply",Map.of("id",b[0],"title",b[1])))
            .collect(Collectors.toList());

        Map<String, Object> interactive = new LinkedHashMap<>();
        interactive.put("type", "button");
        if (header != null)
            interactive.put("header", Map.of("type","text","text",header));
        interactive.put("body",   Map.of("text", bodyText));
        interactive.put("action", Map.of("buttons", btnList));

        Map<String, Object> body = buildBase(toPhone, "interactive");
        body.put("interactive", interactive);
        return call(body);
    }

    // ────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ────────────────────────────────────────────────────────
    private Map<String, Object> buildBase(String toPhone, String type) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("from", fromNumber);
        body.put("to",   toPhone);
        body.put("type", type);
        return body;
    }

    private ChatigoResponse call(Map<String, Object> body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<String> resp = restTemplate.postForEntity(
                baseUrl + "/messages",
                new HttpEntity<>(body, headers),
                String.class
            );
            log.info("Chatigo sent OK to {}: {}", body.get("to"), resp.getBody());
            return new ChatigoResponse(true, resp.getBody(), null);

        } catch (HttpClientErrorException e) {
            log.error("Chatigo error [{}]: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new ChatigoResponse(false, null, e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Chatigo network error: {}", e.getMessage());
            return new ChatigoResponse(false, null, e.getMessage());
        }
    }

    public record ChatigoResponse(boolean success, String rawResponse, String error) {}

}
