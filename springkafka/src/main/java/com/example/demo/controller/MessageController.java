package com.example.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.kafka.KafkaProducer;

@RestController
@RequestMapping("/kafka")
public class MessageController {
	@Autowired
private KafkaProducer kafkaproducer;
	@GetMapping("/get")
	public ResponseEntity<String> getMessage(@RequestParam("message")String message){
		kafkaproducer.sendmessage(message);
		return ResponseEntity.ok("Message sent to the topic");
	}
}
