package com.example.demo;


import com.example.demo.service.ChatigoService;
import com.example.demo.service.InboundMessageHandlerService;
import com.example.demo.service.MessageLoggerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestMessageFlow {
    
    @Autowired
    private ChatigoService chatigoService;
    
    @Autowired
    private InboundMessageHandlerService messageHandler;
    
    @Autowired
    private MessageLoggerService messageLogger;
    
    @Test
    public void testCompleteCarrierJourney() {
        String carrierPhone = "9876543210";
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🧪 TESTING COMPLETE CARRIER JOURNEY");
        System.out.println("=".repeat(80));
        
        // 1. Send load details
        System.out.println("\n📌 Step 1: Sending load details to carrier");
        chatigoService.sendButtons(carrierPhone,
            "Load #SHP-001: Mumbai to Delhi\nWeight: 5.5 Tons\nPrice: ₹25,000",
            "Available Load",
            List.of(new String[]{"accept", "Accept Load"}, new String[]{"decline", "Decline"}));
        
        // 2. Simulate carrier accepting
        System.out.println("\n📌 Step 2: Carrier accepts the load");
        messageHandler.handleButtonReply(carrierPhone, "accept", "Accept Load");
        
        // 3. Simulate pickup confirmation
        System.out.println("\n📌 Step 3: Carrier confirms pickup");
        messageHandler.handleIncomingText(carrierPhone, "PICKUP SHP-001");
        
        // 4. Simulate location update
        System.out.println("\n📌 Step 4: Carrier updates location");
        messageHandler.handleIncomingText(carrierPhone, "LOC Surat Bypass");
        
        // 5. Simulate delivery
        System.out.println("\n📌 Step 5: Carrier confirms delivery");
        messageHandler.handleIncomingText(carrierPhone, "DELIVER SHP-001");
        
        // Generate report
        messageLogger.generateSummaryReport();
        
        System.out.println("\n✅ Test completed! Check whatsapp_messages.log for all messages.");
    }
    
    @Test
    public void testShipperJourney() {
        String shipperPhone = "9876543211";
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🧪 TESTING SHIPPER JOURNEY");
        System.out.println("=".repeat(80));
        
        // Track load
        System.out.println("\n📌 Step 1: Shipper tracks load");
        messageHandler.handleIncomingText(shipperPhone, "TRACK SHP-001");
        
        // Get all loads
        System.out.println("\n📌 Step 2: Shipper requests all loads");
        messageHandler.handleIncomingText(shipperPhone, "MYLOADS");
        
        messageLogger.generateSummaryReport();
        System.out.println("\n✅ Test completed! Check whatsapp_messages.log for all messages.");
    }
}