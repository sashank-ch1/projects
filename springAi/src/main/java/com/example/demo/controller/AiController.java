package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.LocalAiService;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {
	private final LocalAiService aiService;

    public AiController(LocalAiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return aiService.askLocalAi(message);
    }
}
