package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Load;
import com.example.demo.entity.Load.LoadStatus;
import com.example.demo.entity.User;

public interface LoadRepository extends JpaRepository<Load,Long> {

	Optional<Load> findByLoadNumber(String loadNumber);

	List<Load> findByShipperAndStatusNotIn(User shipper, List<LoadStatus> of);

	List<Load> findByStatusIn(List<LoadStatus> of);

	 @Query("SELECT l FROM Load l WHERE  l.estimatedDelivery < :currentTime AND l.status != 'DELIVERED'")
	    List<Load> findOverdueLoads(@Param("currentTime") LocalDateTime currentTime);

}
