package com.example.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="inventory-service")
public interface InventoryClient {

    // Existing: Check if stock exists
    @GetMapping("/inventory/check/{productId}")
    boolean checkStock(@PathVariable("productId") String productId);

    // NEW: Deduct stock after successful payment
    @PostMapping("/inventory/reduce/{productId}")
    void reduceStock(@PathVariable("productId") String productId, @RequestParam("quantity") int quantity);
}
