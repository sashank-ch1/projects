package com.example.demo.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.ChatigoService;
import com.example.demo.service.InboundMessageHandlerService;
import com.example.demo.service.MessageLoggerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhook/chatigo")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/") 
public class ChatigoWebhookController {
	@Value("${chatigo.webhook-secret}")
    private String webhookSecret;
     //
	@Autowired
    private MessageLoggerService messageLogger;
	 @Autowired
	    private ChatigoService chatigoService;
	
	
    private final InboundMessageHandlerService messageHandler;
    
    public ChatigoWebhookController(InboundMessageHandlerService messageHandler) {
    	this.messageHandler=messageHandler;
    }
    
    private static final Logger log=LoggerFactory.getLogger(ChatigoWebhookController.class);
    
   //test
    @PostMapping("/test-all-messages")
    public ResponseEntity<Map<String, Object>> testAllMessageTypes(@RequestParam String to) {
        Map<String, Object> results = new HashMap<>();
        List<String> messages = new ArrayList<>();
        
        // 1. Test Text Message
        ChatigoService.ChatigoResponse textResponse = chatigoService.sendText(to, 
            "Welcome to LogiTrack! This is a test message.");
        messages.add("Text message: " + (textResponse.success() ? "✓ Logged" : "✗ Failed"));
        
        // 2. Test Template Message
        ChatigoService.ChatigoResponse templateResponse = chatigoService.sendTemplate(to,
            "load_posted_carrier",
            List.of("SHP-001", "Mumbai", "Delhi", "5.5", "25000"));
        messages.add("Template message: " + (templateResponse.success() ? "✓ Logged" : "✗ Failed"));
        
        // 3. Test Interactive Buttons
        List<String[]> buttons = List.of(
            new String[]{"confirm_accept", "✅ Accept Load"},
            new String[]{"decline_load", "❌ Decline"},
            new String[]{"call_support", "📞 Call Support"}
        );
        ChatigoService.ChatigoResponse buttonResponse = chatigoService.sendButtons(to,
            "Load #SHP-001 is available for pickup. Would you like to accept?",
            "New Load Available",
            buttons);
        messages.add("Interactive buttons: " + (buttonResponse.success() ? "✓ Logged" : "✗ Failed"));
        
        results.put("success", true);
        results.put("messages", messages);
        results.put("logFile", "whatsapp_messages.log");
        results.put("note", "All messages have been logged to file. No actual WhatsApp messages were sent.");
        
        // Generate summary report
        messageLogger.generateSummaryReport();
        
        return ResponseEntity.ok(results);
    }//test
    
    @PostMapping
    public ResponseEntity<String> receive(
            @RequestHeader(value = "X-Chatigo-Secret", required = false) String secret,
            @RequestBody Map<String, Object> payload) {

        if (!webhookSecret.equals(secret)) {
            log.warn("Webhook: secret mismatch — rejected");
            return ResponseEntity.status(403).body("Forbidden");
        }

        try {
            String type = (String) payload.get("type");
            if ("message".equals(type)) {
                @SuppressWarnings("unchecked")
                Map<String,Object> msg = (Map<String,Object>) payload.get("message");
                String from    = (String) msg.get("from");
                String msgType = (String) msg.get("type");

                if ("text".equals(msgType)) {
                    @SuppressWarnings("unchecked")
                    Map<String,Object> textObj = (Map<String,Object>) msg.get("text");
                    String body = (String) textObj.get("body");
                    messageHandler.handleIncomingText(from, body);

                } else if ("interactive".equals(msgType)) {
                    @SuppressWarnings("unchecked")
                    Map<String,Object> ia = (Map<String,Object>) msg.get("interactive");
                    @SuppressWarnings("unchecked")
                    Map<String,Object> btnReply = (Map<String,Object>) ia.get("button_reply");
                    messageHandler.handleButtonReply(
                        from,
                        (String) btnReply.get("id"),
                        (String) btnReply.get("title")
                    );
                }
            }
        } catch (Exception e) {
            log.error("Webhook error: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok("OK");
    }

}
