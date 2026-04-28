package com.example.demo.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Orders;
import com.example.demo.repository.OrdersRepository;
import com.example.demo.service.OrderService;


@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    public OrderService service;
    @Autowired
    public OrdersRepository ordrepo;

    @PostMapping("/{productId}")
    public ResponseEntity<?> createOrder(
            @PathVariable String productId, 
            @RequestBody java.util.Map<String, String> details) { // 👈 Capture JSON body
        try {
            String address = details.get("address");
            String paymentMode = details.get("paymentMode");
            String userId = details.get("userId");
            // Pass address and paymentMode to service
            Orders placedOrder = service.placeOrder(productId,address, paymentMode, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(placedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Orders> getOrderById(@PathVariable("orderId") String orderid) {
        // FIX: Call findByOrderid (lowercase 'i')
        return ordrepo.findByOrderid(orderid) 
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/place") // Total path: /api/orders/place
    public ResponseEntity<Orders> placeOrder(
            @RequestParam String productId,
            @RequestParam String address,
            @RequestParam String paymentMode,
            @RequestParam String userId) {
        
        Orders order = service.placeOrder(productId, address, paymentMode, userId);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/all")
    public List<Orders> getAllOrders() {
        // This uses the built-in findAll() from JpaRepository
        return ordrepo.findAll();
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Orders>> getOrdersByUserId(@PathVariable String userId) {
        List<Orders> orders = service.getOrdersByUserId(userId);
        // ✅ NO .map() here because it's a List
        return ResponseEntity.ok(orders);
    }
    @DeleteMapping("/cancel")
    public String Cancelorder(Long id) {
    	return service.deleteorder(id);
    }
    @PutMapping("/feedback/{orderId}")
    public ResponseEntity<Orders> updateFeedback(@PathVariable String orderId, @RequestBody Map<String, Object> payload) {
        Orders order = ordrepo.findByOrderid(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
            
        // Extract both values
        order.setfeedback((String) payload.get("feedback"));
        order.setrating((Integer) payload.get("rating"));
        
        return ResponseEntity.ok(ordrepo.save(order));
    }
}
