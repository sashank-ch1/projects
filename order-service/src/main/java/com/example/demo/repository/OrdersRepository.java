package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Orders;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUserId(String userId);

    // FIX: Changed 'OrderId' to 'Orderid' (lowercase 'i')
    Optional<Orders> findByOrderid(String orderid);

	
}
