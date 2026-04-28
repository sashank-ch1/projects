package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User;
import com.example.demo.entity.User.UserRole;

public interface UserRepository extends JpaRepository<User,Long> {

	List<User> findByRoleAndActiveTrue(UserRole carrier);
	Optional<User> findByPhone(String phone);
}
