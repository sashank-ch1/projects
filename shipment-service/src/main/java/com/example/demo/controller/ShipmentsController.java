package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ShipmentsDTO;
import com.example.demo.entity.Shipments;
import com.example.demo.repository.ShipmentsRepository;
import com.example.demo.service.ShipmentsService;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentsController {
	 @Autowired
	    private ShipmentsService service;
	 @Autowired
	 private ShipmentsRepository shiprepo;
	 
	 @GetMapping("/admin/all")
	    public List<Shipments> getAllShipments() {
	        return shiprepo.findAll();
	    }

	    // For Carrier: See only their assigned tasks
	 @GetMapping("/carrier/{carriername}") // Changed to lowercase 'n'
	 public List<Shipments> getMyShipments(@PathVariable String carriername) {
	     return shiprepo.findByCarriername(carriername); // Changed to lowercase 'n'
	 }
	 
	 @GetMapping("/order/{orderid}")
	 public ResponseEntity<Shipments> getTrackingByOrder(@PathVariable String orderid) {
	     return shiprepo.findByOrderid(orderid)
	             .map(ResponseEntity::ok)
	             .orElse(ResponseEntity.notFound().build());
	 }

	 
	 @PostMapping("/create")
	    public ResponseEntity<Shipments> processShipment(@RequestBody ShipmentsDTO dto) {
	        // Convert DTO to Entity
	        Shipments shipment = new Shipments();
	        shipment.setOrderid(dto.getOrderid());
	        shipment.setCarriername(dto.getCarriername());
	        shipment.setPaymentstatus(dto.getPaymentstatus());
	        // (Note: Your entity doesn't have shippingAddress yet, you might want to add it!)

	        return ResponseEntity.ok(service.createShipment(shipment));
	    }
	 @PutMapping("/{id}")
	 public ResponseEntity<Shipments> updateShipment(@PathVariable Long id, @RequestBody ShipmentsDTO updateDto) {
	     return ResponseEntity.ok(service.updateShipment(id, updateDto));
	 }

 @PutMapping("/return/{ids}")
 public ResponseEntity<Shipments> returnRequest(@PathVariable("ids") Long id, @RequestBody ShipmentsDTO updateDto) {
     return ResponseEntity.ok(service.updatereturnstatus(id, updateDto));
 }
}
