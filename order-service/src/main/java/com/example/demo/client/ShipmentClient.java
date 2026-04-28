package com.example.demo.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ShipmentsDTO;
@FeignClient(name = "shipment-service")
public interface ShipmentClient {
	 @PostMapping("/api/shipments/create")
	    ShipmentsDTO initiateShipment(@RequestBody ShipmentsDTO shipment);
	 
	 
	 
}
