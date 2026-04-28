package com.example.demo.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
@Data
@Builder	
public class WhatsAppMessageRequest {
	 private String messaging_product;  // always "whatsapp"
	    private String to;                 // recipient phone with country code
	    private String type;               // "text", "template", etc.
	    private TextBody text;

	    @Data
	    @AllArgsConstructor
	    public static class TextBody {
	        private boolean preview_url;
	        private String body;
	    }

		
}
