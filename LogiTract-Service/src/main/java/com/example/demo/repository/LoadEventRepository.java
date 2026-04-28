package com.example.demo.repository;
import com.example.demo.entity.LoadEvent;
import com.example.demo.entity.Load;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoadEventRepository extends JpaRepository<LoadEvent, Long> {

    // Get all events for a specific load (timeline)
    List<LoadEvent> findByLoadOrderByTimestampAsc(Load load);

    // Optional: get latest event
    List<LoadEvent> findTop1ByLoadOrderByTimestampDesc(Load load);
}
