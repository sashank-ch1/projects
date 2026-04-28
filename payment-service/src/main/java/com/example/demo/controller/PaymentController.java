package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.service.PaymentService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    
    @Autowired
    public PaymentService payserv;
    @PostMapping("/pay")
    public String process(
            @RequestParam("amount") Double amount, 
            @RequestParam("userId") Long userId, 
            @RequestBody Map<String, Object> details) {
        
        System.out.println("Processing payment for User: " + userId + " Amount: " + amount);
        
        // Your logic here...
        return "SUCCESS";
    }
    
    @PostMapping("/pays")
    public String pay(@RequestParam("userId") String userId, @RequestParam("amount") double amount,@RequestParam("status") String status) {
        // DEBUG: This will confirm if the data actually arrived
        System.out.println("PAYMENT_SERVICE RECEIVED: User=" + userId + ", Amount=" + amount);
        
        if (userId == null || userId.equals("null")) {
            throw new RuntimeException("ERROR: userId is missing in request!");
        }
        return payserv.processPayment(userId, amount,status);
    }

}


