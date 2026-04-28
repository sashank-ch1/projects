package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Products;
import com.example.demo.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;
      
    
    
    // ✅ CREATE (used by POST)
    public Products saveProduct(Products product) {
        return repo.save(product);
    }

    // ✅ GET ALL
    public List<Products> getproducts() {
        return repo.findAll();
    }

    // ✅ GET SINGLE (FIXED)
    public Products getProduct(String productId) {
        Long id = Long.parseLong(productId);
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // ✅ UPDATE (FIXED)
    public Products updateProduct(Long id, Products product) {
        product.setId(id);
        return repo.save(product);
    }

    // ✅ DELETE (FIXED)
    public void delproduct(Long id) {
        repo.deleteById(id);
    }

    // ✅ FEATURED (TEMP LOGIC)
    public List<Products> getFeaturedProducts() {
        return repo.findAll(); // 🔥 replace later with real logic
    }

    // ✅ NEW ARRIVALS (TEMP LOGIC)
    public List<Products> getNewArrivals() {
        return repo.findAll(); // 🔥 replace later with sorting
    }

    // ✅ SEARCH
    public List<Products> searchProducts(String query) {
        return repo.findByNameContainingIgnoreCase(query);
    }
    public List<String> getCategories() {
        List<Products> allProducts = repo.findAll();
        if (allProducts == null || allProducts.isEmpty()) {
            return new ArrayList<>(); // Return empty list instead of crashing
        }
        return allProducts.stream()
                .map(Products::getCategory)
                .filter(cat -> cat != null && !cat.isEmpty()) // Prevent NullPointer
                .distinct()
                .toList();
    }
}