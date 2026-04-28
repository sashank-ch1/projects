package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class MessageLoggerService {
    
    private static final String LOG_FILE = "whatsapp_messages.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Logger log=LoggerFactory.getLogger(MessageLoggerService.class);
    public void logTextMessage(String toPhone, String message) {
        String logEntry = String.format(
            "\n%s | [TEXT MESSAGE] | To: %s | Message: %s\n%s\n",
            LocalDateTime.now().format(formatter),
            toPhone,
            message,
            "─".repeat(80)
        );
        
        // Log to console
        log.info("📝 WOULD SEND TEXT MESSAGE:");
        log.info("   To: {}", toPhone);
        log.info("   Message: {}", message);
        
        // Write to file
        writeToFile(logEntry);
    }
    
    public void logTemplateMessage(String toPhone, String templateName, List<String> params) {
        String logEntry = String.format(
            "\n%s | [TEMPLATE MESSAGE] | To: %s | Template: %s | Params: %s\n%s\n",
            LocalDateTime.now().format(formatter),
            toPhone,
            templateName,
            String.join(", ", params),
            "─".repeat(80)
        );
        
        // Log to console
        log.info("📝 WOULD SEND TEMPLATE MESSAGE:");
        log.info("   To: {}", toPhone);
        log.info("   Template: {}", templateName);
        log.info("   Parameters: {}", params);
        
        // Write to file
        writeToFile(logEntry);
    }
    
    public void logInteractiveMessage(String toPhone, String bodyText, String header, List<String[]> buttons) {
        StringBuilder buttonsStr = new StringBuilder();
        for (String[] btn : buttons) {
            buttonsStr.append(String.format("\n      - %s: %s", btn[0], btn[1]));
        }
        
        String logEntry = String.format(
            "\n%s | [INTERACTIVE MESSAGE] | To: %s | Header: %s | Body: %s | Buttons:%s\n%s\n",
            LocalDateTime.now().format(formatter),
            toPhone,
            header != null ? header : "None",
            bodyText,
            buttonsStr.toString(),
            "─".repeat(80)
        );
        
        // Log to console
        log.info("📝 WOULD SEND INTERACTIVE MESSAGE:");
        log.info("   To: {}", toPhone);
        log.info("   Header: {}", header);
        log.info("   Body: {}", bodyText);
        log.info("   Buttons:");
        for (String[] btn : buttons) {
            log.info("     - {}: {}", btn[0], btn[1]);
        }
        
        // Write to file
        writeToFile(logEntry);
    }
    
    private void writeToFile(String logEntry) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.print(logEntry);
        } catch (Exception e) {
            log.error("Failed to write to log file: {}", e.getMessage());
        }
    }
    
    public void generateSummaryReport() {
        String report = String.format(
            "\n%s\n📊 WHATSAPP MESSAGE SUMMARY REPORT\n%s\nGenerated at: %s\n\n",
            "=".repeat(80),
            "=".repeat(80),
            LocalDateTime.now().format(formatter)
        );
        writeToFile(report);
        log.info("Message summary report generated in: {}", LOG_FILE);
    }
}
