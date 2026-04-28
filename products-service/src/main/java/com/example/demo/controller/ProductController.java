package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Products;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService proserv;
    @Autowired
    private ProductRepository productRepository;

    @PutMapping("/{productId}/restock")
    public ResponseEntity<?> restockProduct(
            @PathVariable String productId, 
            @RequestParam int amount) {
        
        return productRepository.findByProductId(productId)
            .map(product -> {
                product.setQunty(product.getQunty() + amount); // Add to existing stock
                productRepository.save(product);
                return ResponseEntity.ok("Stock updated successfully. New stock: " + product.getQunty());
            })
            .orElse(ResponseEntity.notFound().build());
    }
            
    // ✅ GET ALL PRODUCTS
    @GetMapping
    public List<Products> getAllProducts() {
        return proserv.getproducts();
    }

    // ✅ GET SINGLE PRODUCT (FIXED)
    @GetMapping("/{productId}")
    public Products getProduct(@PathVariable("productId") String productId) {
        return productRepository.findByProductId(productId) 
               .orElseThrow(() -> new RuntimeException("Product Not Found"));
    }


    // ✅ FEATURED
    @GetMapping("/featured")
    public List<Products> getFeaturedProducts() {
        return proserv.getFeaturedProducts();
    }

    // ✅ NEW ARRIVALS
    @GetMapping("/new-arrivals")
    public List<Products> getNewArrivals() {
        return proserv.getNewArrivals();
    }

    // ✅ SEARCH
    @GetMapping("/search")
    public List<Products> searchProducts(@RequestParam("q") String query) {
        return proserv.searchProducts(query);
    }

    // ✅ CATEGORIES
    @GetMapping("/categories")
    public List<String> getCategories() {
        return proserv.getCategories();
    }

    // ✅ CREATE
    @PostMapping
    public Products createProduct(@RequestBody Products product) {
        return proserv.saveProduct(product);
    }

    // ✅ UPDATE (FIXED)
    @PutMapping("/{id}")
    public Products updateProduct(@PathVariable Long id, @RequestBody Products product) {
        return proserv.updateProduct(id, product);
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        proserv.delproduct(id);
    }
}