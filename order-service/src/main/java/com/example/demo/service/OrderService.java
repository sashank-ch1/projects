package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.client.InventoryClient;
import com.example.demo.client.PaymentClient;
import com.example.demo.client.ProductClient;
import com.example.demo.client.ShipmentClient;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ShipmentsDTO;
import com.example.demo.entity.Orders;
import com.example.demo.repository.OrdersRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrdersRepository ordrepo;

    @Autowired
    private InventoryClient invcli;

    @Autowired
    private PaymentClient paycli;
    
    @Autowired
    private ProductClient productClient;

    // --- ADDED THIS AUTOWIRED INSTANCE ---
    @Autowired
    private ShipmentClient shipmentClient;

    @Transactional
    public Orders placeOrder(String productId, String address, String paymentMode, String userId) {
        System.out.println("ORDER_SERVICE: Placing order for UserID: " + userId);
        
        // 1. Check for duplicate orders
        List<Orders> recentOrders = ordrepo.findByUserId(userId);
        boolean isDuplicate = recentOrders.stream()
                .anyMatch(o -> productId.equals(o.getProductid()) && "SUCCESS".equals(o.getorderstatus()));
        
        if (isDuplicate) {
            throw new RuntimeException("Duplicate order detected for this product.");
        }

        // 2. Fetch Product Details
        ProductDTO product;
        try {
            product = productClient.getProduct(productId);
        } catch (Exception e) {
            throw new RuntimeException("CRITICAL: Products-Service is failing to find ID: " + productId + ". Error: " + e.getMessage());
        }

        if (product == null) {
            throw new RuntimeException("Product " + productId + " does not exist in Products Database.");
        }

        // 3. Inventory Check
        boolean inStock = invcli.checkStock(product.getProductId()); 
        if (!inStock) {
            throw new RuntimeException("Product Out of Stock: " + product.getProductId());
        }

        // 4. THE FIX: Define Amount and Payment Params
        Double amount = product.getPrice(); 
        Map<String, Object> params = new HashMap<>();
        
        params.put("paymentMode", paymentMode);
        params.put("productName", product.getName());
        Long numericUserId = Long.valueOf(userId); 
        if (numericUserId == null) {
            throw new RuntimeException("UserID is required for payment");
        }

        String paymentStatus = paycli.pay(amount, Long.valueOf(userId), params);

        if (!"SUCCESS".equalsIgnoreCase(paymentStatus)) {
             throw new RuntimeException("Payment Failed: " + paymentStatus);
        }

        // 6. Reduce Stock
        try {
            invcli.reduceStock(product.getProductId(), 1); 
        } catch (Exception e) {
            throw new RuntimeException("Failed to update inventory: " + e.getMessage());
        }
        
        // 7. Create and Save Order
        Orders order = new Orders();
        String uniqueTrackingId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        order.setuserid(userId); 
        order.setorderid(uniqueTrackingId); 
        order.setProductid(product.getProductId());
        order.setProname(product.getName());
        order.setPrice(product.getPrice());
        order.setaddress(address);           
        order.setPaymentMode(paymentMode); 
        order.setorderstatus("SUCCESS");
       
        Orders savedOrder;
        try {
            savedOrder = ordrepo.save(order);
        } catch (Exception e) {
            throw new RuntimeException("FAILED: Could not save order to database");
        }

        // --- NEW LOGIC MOVED HERE (Fixes Static Reference Errors) ---
     // Inside placeOrder method, after savedOrder = ordrepo.save(order);
        try {
            ShipmentsDTO shipmentData = new ShipmentsDTO();
            // 1. Ensure this is a String in your DTO!
            shipmentData.setOrderid(savedOrder.getorderid()); 
            shipmentData.setPaymentstatus("PENDING");

            // 2. Call the @Autowired instance (shipmentClient), not the class
            shipmentClient.initiateShipment(shipmentData); 
            System.out.println("SUCCESS: Shipment created for " + savedOrder.getorderid());
        } catch (Exception e) {
            // 3. THIS PREVENTS THE 500 ERROR. 
            // Even if shipment fails, the user gets their "Order Successful" response.
            System.err.println("SHIPMENT ERROR: " + e.getMessage());
        }

        return savedOrder;
    }

    public List<Orders> getOrdersByUserId(String userId) {
        return ordrepo.findByUserId(userId);
    }
    public String deleteorder(Long id) {
    	ordrepo.deleteById(id);
    	return "Order cancled by the user";
    }
   
}

