package com.example.demo.controller;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Load;
import com.example.demo.entity.User;
import com.example.demo.service.LoadService;
import com.example.demo.service.UserService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/loads")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/") 
public class LoadController {

    private final LoadService loadService;
   
    
    private static Logger log=LoggerFactory.getLogger(LoadController.class);
    
    public LoadController(LoadService loadService) {
		super();
		this.loadService = loadService;
		//this.userService=userService;
	}
    
    
    // POST /api/v1/loads — Shipper posts a new load
    @PostMapping
    public ResponseEntity<Load> postLoad(@RequestBody Map<String,Object> req) {
        // In production: get shipperId from JWT security context, not request body
    	Load load = new Load();

        load.setOriginCity((String) req.get("originCity"));
        load.setDestinationCity((String) req.get("destinationCity"));
        load.setOriginAddress((String) req.get("originAddress"));
        load.setDestinationAddress((String) req.get("destinationAddress"));
        load.setCargoType((String) req.get("cargoType"));
        load.setWeightTons(new BigDecimal(req.get("weightTons").toString()));
        load.setQuotedPrice(new BigDecimal(req.get("quotedPrice").toString()));
      
        
        return ResponseEntity.ok(loadService.postLoad(load));
    }

    // PATCH /api/v1/loads/{loadNumber}/pickup — Carrier confirms pickup
    @PatchMapping("/{loadNumber}/pickup")
    public ResponseEntity<Load> confirmPickup(
            @PathVariable String loadNumber,
            @RequestBody Map<String,Object> req) {
        Long carrierId = Long.valueOf(req.get("carrierId").toString());
        String notes   = (String) req.getOrDefault("notes", "Pickup confirmed");
        return ResponseEntity.ok(loadService.confirmPickup(loadNumber, carrierId, notes));
    }

    // PATCH /api/v1/loads/{loadNumber}/location — Carrier updates location
    @PatchMapping("/{loadNumber}/location")
    public ResponseEntity<Load> updateLocation(
            @PathVariable String loadNumber,
            @RequestBody Map<String,Object> req) {
        return ResponseEntity.ok(loadService.updateLocation(
            loadNumber,
            Long.valueOf(req.get("carrierId").toString()),
            (String) req.get("location"),
            (String) req.get("eta")
        ));
    }

    // PATCH /api/v1/loads/{loadNumber}/deliver — Carrier confirms delivery
    @PatchMapping("/{loadNumber}/deliver")
    public ResponseEntity<Load> confirmDelivery(
            @PathVariable String loadNumber,
            @RequestBody Map<String,Object> req) {
        return ResponseEntity.ok(loadService.confirmDelivery(
            loadNumber,
            Long.valueOf(req.get("carrierId").toString()),
            (String) req.getOrDefault("notes", "Delivered")
        ));
    }

	
}
