package com.example.demo.controller;


import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.WhatsAppDTO;
import com.example.demo.service.WhatsAppService;
import com.example.demo.service.ChatigoService; // Added import

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppController {
    
    private final WhatsAppService whatsAppService;
    private final ChatigoService chatigoService; // Added this field

    // Updated constructor to include both services
    public WhatsAppController(WhatsAppService whatsAppService, ChatigoService chatigoService) {
        this.whatsAppService = whatsAppService;
        this.chatigoService = chatigoService;
    }
    
    @PostMapping("/send-text")
    public ResponseEntity<String> sendText(@RequestBody WhatsAppDTO dto) {
        // Using the DTO passed in the method argument
        return ResponseEntity.ok(whatsAppService.sendTextMessage(dto.getPhone(), dto.getMessage()));
    }

    @PostMapping("/chatigo/send")
    public ResponseEntity<String> sendChatigo(@RequestBody WhatsAppDTO dto) {
        // Now chatigoService is properly recognized
        return ResponseEntity.ok(chatigoService.sendMessage(dto.getPhone(), dto.getMessage()));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody WhatsAppDTO dto) {
        // Updated this to use the DTO for consistency
        String response = whatsAppService.sendTemplateMessage(
            dto.getPhone(), "otp_template", "en_US", dto.getOtp());
        return ResponseEntity.ok(response);
    }
}
