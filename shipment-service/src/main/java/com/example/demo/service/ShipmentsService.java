package com.example.demo.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ShipmentsDTO;
import com.example.demo.entity.Shipments;
import com.example.demo.repository.ShipmentsRepository;

@Service
public class ShipmentsService {
	 @Autowired
	    private ShipmentsRepository repository;

	 public Shipments createShipment(Shipments shipment) {
	        // 1. Generate Tracking ID
	        shipment.setTrackingid("TRK-" + System.currentTimeMillis());
	        
	        // 2. Set Default Expected Delivery (7 Days from now)
	        Calendar cal = Calendar.getInstance();
	        cal.add(Calendar.DAY_OF_MONTH, 7);
	        shipment.setEstimateddelivery(cal.getTime());
	        
	        shipment.setShippedat(new Date());
	        return repository.save(shipment);
	    }
	    
	    
	    
	    
	    public Shipments updateShipment(Long id, ShipmentsDTO dto) {
	        Shipments existingShipment = repository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + id));

	        // Update fields from DTO
	        if (dto.getCarriername() != null) existingShipment.setCarriername(dto.getCarriername());
	        if (dto.getPaymentstatus() != null) existingShipment.setPaymentstatus(dto.getPaymentstatus());
	        
	        // Logic for tracking updates
	        existingShipment.setEstimateddelivery(new Date()); // Example: Refreshing date on update
	        
	        return repository.save(existingShipment);
	    }
	    
	    public Shipments updatereturnstatus(Long id,ShipmentsDTO dto) {
	    	Shipments returnstatus=repository.findById(id)
	    			.orElseThrow(() -> new RuntimeException("Shipment not found with id: " + id));;
	    	return repository.save(returnstatus);
	    	
	    }
}
