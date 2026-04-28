package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entity.Inventory;
import com.example.demo.repository.InventoryRepo;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepo invrepo;

    // Check if the product is in stock
    public boolean isInStock(String productId) {
        return invrepo.findByProductId(productId)
                .map(inventory -> inventory.getQunty() > 0)
                .orElse(false);
    }

    // Reduce stock when an order is successfully placed
    @Transactional
    public void reduceStock(String productId, int quantity) {
        Inventory inventory = invrepo.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found in inventory: " + productId));

        if (inventory.getQunty() < quantity) {
            throw new RuntimeException("Insufficient stock for product ID: " + productId);
        }

        inventory.setQunty(inventory.getQunty() - quantity);
        invrepo.save(inventory);
    }

    // Add or update stock (Used by Admin Dashboard)
    public void updateStock(String productId, int quantity) {
        Inventory inventory = invrepo.findByProductId(productId).orElse(new Inventory());
        inventory.setProductid(productId);
        inventory.setQunty(quantity);
        invrepo.save(inventory);
    }
}
