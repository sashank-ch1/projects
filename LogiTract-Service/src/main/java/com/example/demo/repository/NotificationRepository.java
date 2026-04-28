package com.example.demo.repository;
import com.example.demo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // You might need this later to track history for a specific load
    List<Notification> findByLoadId(Long loadId);
    
    // Or to find notifications for a specific user
    List<Notification> findByRecipientId(Long userId);

}
