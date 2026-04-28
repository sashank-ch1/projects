package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class LocalAiService {

	private final ChatClient chatClient;            
	
	  // 1. Constructor Injection of ChatClient.Builder
    public LocalAiService(ChatClient.Builder builder) {
        // 2. Build the ChatClient instance
    	
        this.chatClient = builder
        		.defaultSystem("You are a helpful assistant for an E-commerce Application") //this default system says that the ai is a helper for e-commerce platform
        		.build();
    }

    public String askLocalAi(String userInput) {
        // 3. Fluent API to send prompt and get response
        return this.chatClient.prompt()  // 1. Prepare a new request structure
                .user(userInput) // 2. Wrap your text in a UserMessage object
                .call()                    // 3. Connect to http://localhost:11434/api/chat
                .content();                // 4. Parse the JSON response to extract text
    }
    
   
    }

	

