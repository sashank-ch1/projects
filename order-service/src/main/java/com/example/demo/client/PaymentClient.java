package com.example.demo.client;

import java.util.Map;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="payment-service")
public interface PaymentClient {
    
    @PostMapping("/payment/pay")
    // Using @SpringQueryMap forces the Map keys to become URL parameters: ?userId=...&amount=...
    String pay(
            @RequestParam("amount") Double amount, 
            @RequestParam("userId") Long userId,
            @RequestBody Map<String, Object> details
        );
}

