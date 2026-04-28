package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.kafka.JsonKafkaProducer;
import com.example.demo.payload.User;

@RestController
@RequestMapping("/json")
public class JsonMessageController {
	@Autowired
public JsonKafkaProducer kafkaproducer;
	@PostMapping("/add")
	public ResponseEntity<String> publish(User user){
		kafkaproducer.sendmessage(user);
		return ResponseEntity.ok("Json Message posted sucessfully");
	}
}
