package com.example.demo.controller;
import org.springframework.web.bind.annotation.*;



import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
@RestController
@RequestMapping("/webhook")
@Slf4j
public class WhatsAppWebhookController {
	@Value("${whatsapp.verify-token}")
    private String verifyToken;
  
	 private static final Logger log = LoggerFactory.getLogger(WhatsAppWebhookController.class);
	
    // ✅ Meta calls this to verify your webhook
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            log.info("Webhook verified successfully");
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(403).body("Forbidden");
    }

    // ✅ Meta sends incoming messages here
    public ResponseEntity<String> receiveMessage(@RequestBody Map<String, Object> payload) {
        try {
            // Safe check for the nested structure
            List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");
            if (entries == null || entries.isEmpty()) return ResponseEntity.ok("NO_ENTRY");

            List<Map<String, Object>> changes = (List<Map<String, Object>>) entries.get(0).get("changes");
            if (changes == null || changes.isEmpty()) return ResponseEntity.ok("NO_CHANGES");

            Map<String, Object> value = (Map<String, Object>) changes.get(0).get("value");
            
            // 🚨 IMPORTANT: Meta sends 'statuses' and 'messages' separately
            if (value.containsKey("messages")) {
                List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
                Map<String, Object> msg = messages.get(0);
                String from = (String) msg.get("from");
                
                if ("text".equals(msg.get("type"))) {
                    Map<String, Object> text = (Map<String, Object>) msg.get("text");
                    String body = (String) text.get("body");
                    log.info("New Message from {}: {}", from, body);
                }
            } else if (value.containsKey("statuses")) {
                // This is just a 'Delivered' or 'Read' receipt, ignore it for now
                log.info("Received a message status update.");
            }

        } catch (Exception e) {
            log.error("Parsing error: {}", e.getMessage());
        }
        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}
