package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "load_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoadEvent {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many events belong to one Load
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "load_id", nullable = false)
    private Load load;

    // Event type like LOAD_POSTED, CARRIER_ACCEPTED, etc.
    @Column(nullable = false)
    private String eventType;

    // Who triggered this event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggered_by")
    private User triggeredBy;

    // Description (human-readable)
    @Column(length = 1000)
    private String description;

    // Optional location (used in tracking)
    private String location;

    // Timestamp of event
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Auto-set timestamp before saving
    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Load getLoad() {
		return load;
	}

	public void setLoad(Load load) {
		this.load = load;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public User getTriggeredBy() {
		return triggeredBy;
	}

	public void setTriggeredBy(User triggeredBy) {
		this.triggeredBy = triggeredBy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
    
}
