package com.example.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.ProductDTO;

@FeignClient(name="products-service")
public interface ProductClient {
    @GetMapping("/api/products/{productId}")
	ProductDTO getProduct(@PathVariable("productId") String productId);

}
