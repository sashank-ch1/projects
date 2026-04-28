package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loads")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Load {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "load_number", unique = true, nullable = false)
    private String loadNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id", nullable = true)
    private User shipper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id")
    private User carrier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = true)
    private User receiver;

    private String originCity;
    private String destinationCity;
    private String originAddress;
    private String destinationAddress;
    private String cargoType;

    private BigDecimal weightTons;
    private BigDecimal quotedPrice;

    @Enumerated(EnumType.STRING)
    private LoadStatus status;

    private LocalDateTime scheduledPickup;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime actualPickup;
    private LocalDateTime actualDelivery;

    private String currentLocation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum LoadStatus {
        POSTED, CARRIER_ACCEPTED, PICKUP_CONFIRMED, IN_TRANSIT,
        DELAYED, OUT_FOR_DELIVERY, DELIVERED, DELIVERY_FAILED,
        CANCELLED, DISPUTED
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLoadNumber() {
		return loadNumber;
	}

	public void setLoadNumber(String loadNumber) {
		this.loadNumber = loadNumber;
	}

	public User getShipper() {
		return shipper;
	}

	public void setShipper(User shipper) {
		this.shipper = shipper;
	}

	public User getCarrier() {
		return carrier;
	}

	public void setCarrier(User carrier) {
		this.carrier = carrier;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public String getOriginCity() {
		return originCity;
	}

	public void setOriginCity(String originCity) {
		this.originCity = originCity;
	}

	public String getDestinationCity() {
		return destinationCity;
	}

	public void setDestinationCity(String destinationCity) {
		this.destinationCity = destinationCity;
	}

	public String getOriginAddress() {
		return originAddress;
	}

	public void setOriginAddress(String originAddress) {
		this.originAddress = originAddress;
	}

	public String getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	public String getCargoType() {
		return cargoType;
	}

	public void setCargoType(String cargoType) {
		this.cargoType = cargoType;
	}

	public BigDecimal getWeightTons() {
		return weightTons;
	}

	public void setWeightTons(BigDecimal weightTons) {
		this.weightTons = weightTons;
	}

	public BigDecimal getQuotedPrice() {
		return quotedPrice;
	}

	public void setQuotedPrice(BigDecimal quotedPrice) {
		this.quotedPrice = quotedPrice;
	}

	public LoadStatus getStatus() {
		return status;
	}

	public void setStatus(LoadStatus status) {
		this.status = status;
	}

	public LocalDateTime getScheduledPickup() {
		return scheduledPickup;
	}

	public void setScheduledPickup(LocalDateTime scheduledPickup) {
		this.scheduledPickup = scheduledPickup;
	}

	public LocalDateTime getEstimatedDelivery() {
		return estimatedDelivery;
	}

	public void setEstimatedDelivery(LocalDateTime estimatedDelivery) {
		this.estimatedDelivery = estimatedDelivery;
	}

	public LocalDateTime getActualPickup() {
		return actualPickup;
	}

	public void setActualPickup(LocalDateTime actualPickup) {
		this.actualPickup = actualPickup;
	}

	public LocalDateTime getActualDelivery() {
		return actualDelivery;
	}

	public void setActualDelivery(LocalDateTime actualDelivery) {
		this.actualDelivery = actualDelivery;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
    
}


