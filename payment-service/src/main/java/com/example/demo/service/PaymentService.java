package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Payment;
import com.example.demo.repository.PaymentRepo;

@Service
public class PaymentService {
    @Autowired
    public PaymentRepo payrepo;
    
    public String processPayment(String userId, double amount,String status) {
        // 1. Create the entity
        Payment p = new Payment();
        
        // 2. CRITICAL FIX: Map the data received from the Controller/Feign
        p.setUserId(userId);   // 👈 This prevents the NULL database error
        p.setAmount(amount);   // 👈 This ensures the payment record is accurate
        p.setStatus(status);
        
        try {
            // 3. Save to database
            payrepo.save(p);
            System.out.println("Payment saved successfully for User: " + userId);
            return "SUCCESS";
        } catch (Exception e) {
            // Log the actual DB error
            System.err.println("Database Error during payment save: " + e.getMessage());
            return "FAILED";
        }
    }
}
