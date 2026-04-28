package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Products;
@Repository
public interface ProductRepository extends JpaRepository<Products,Long> {

	// ✅ REQUIRED FOR SEARCH
    List<Products> findByNameContainingIgnoreCase(String name);
    Optional<Products> findByProductId(String productId); 
}
