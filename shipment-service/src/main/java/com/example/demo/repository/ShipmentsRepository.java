package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Shipments;

public interface ShipmentsRepository extends JpaRepository<Shipments,Long> {

	List<Shipments> findByCarriername(String carriername); 
	Optional<Shipments> findByOrderid(String orderid);

}
