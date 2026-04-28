package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.InventoryService;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // Used by Order Service to check availability
    @GetMapping("/check/{productId}")
    public boolean checkStock(@PathVariable String productId) {
        return inventoryService.isInStock(productId);
    }

    // Used by Order Service after successful payment
    @PostMapping("/reduce/{productId}")
    public ResponseEntity<String> reduce(@PathVariable String productId, @RequestParam int quantity) {
        try {
            inventoryService.reduceStock(productId, quantity);
            return ResponseEntity.ok("Stock reduced successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Used by Admin Dashboard to set stock levels
    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestParam String productId, @RequestParam int quantity) {
        inventoryService.updateStock(productId, quantity);
        return ResponseEntity.ok("Inventory updated for product: " + productId);
    }
}
