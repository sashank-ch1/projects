package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Load;
import com.example.demo.entity.Load.LoadStatus;
import com.example.demo.entity.LoadEvent;
import com.example.demo.entity.User;
import com.example.demo.repository.LoadEventRepository;
import com.example.demo.repository.LoadRepository;
import com.example.demo.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoadService {
	private final LoadRepository       loadRepository;
    private final UserRepository       userRepository;
    private final LoadEventRepository  eventRepository;
    private final NotificationService  notificationService;
 
    private static final Logger log = LoggerFactory.getLogger(LoadService.class);
    
    public LoadService(LoadRepository loadRepository,
    		UserRepository userRepository,
    		LoadEventRepository eventRepository,
    		NotificationService  notificationService) {
    	this.loadRepository=loadRepository;
    	this.userRepository=userRepository;
    	this.eventRepository=eventRepository;
    	this.notificationService=notificationService;
    }
    // ════════════════════════════════════════════════════════
    // ACTION 1: Shipper posts a new load
    // ════════════════════════════════════════════════════════
    @Transactional
    public Load postLoad(Load load) {
    	// Generate unique load number: SHP-YYYYMMDD-SEQ
        load.setLoadNumber(generateLoadNumber());
        load.setStatus(LoadStatus.POSTED);
        load = loadRepository.save(load);

        recordEvent(load, "LOAD_POSTED", load.getShipper(), "Load posted to marketplace", null);

        // Find all active carriers and notify them
        List<User> carriers = userRepository.findByRoleAndActiveTrue(User.UserRole.CARRIER);
        notificationService.notifyCarriersOfNewLoad(load, carriers);

        log.info("Load posted: {} | Notified {} carriers", load.getLoadNumber(), carriers.size());
        return load;
    }

    // ════════════════════════════════════════════════════════
    // ACTION 2: Carrier accepts a load
    // ════════════════════════════════════════════════════════
    @Transactional
    public Load acceptLoad(String loadNumber, Long carrierId) {
        Load load = findByNumber(loadNumber);
        User carrier = userRepository.findById(carrierId)
            .orElseThrow(() -> new RuntimeException("Carrier not found: " + carrierId));

        // Validate transition
        validateTransition(load, LoadStatus.CARRIER_ACCEPTED);

        load.setCarrier(carrier);
        load.setStatus(LoadStatus.CARRIER_ACCEPTED);
        load = loadRepository.save(load);

        recordEvent(load, "CARRIER_ACCEPTED", carrier, carrier.getCompanyName() + " accepted load", null);
        notificationService.notifyCarrierAccepted(load);

        log.info("Load {} accepted by carrier {}", loadNumber, carrier.getCompanyName());
        return load;
    }

    // ════════════════════════════════════════════════════════
    // ACTION 3: Carrier confirms pickup
    // ════════════════════════════════════════════════════════
    @Transactional
    public Load confirmPickup(String loadNumber, Long carrierId, String pickupNotes) {
        Load load = findByNumber(loadNumber);
        validateCarrierOwnership(load, carrierId);
        validateTransition(load, LoadStatus.PICKUP_CONFIRMED);

        load.setStatus(LoadStatus.PICKUP_CONFIRMED);
        load.setActualPickup(LocalDateTime.now());
        load = loadRepository.save(load);

        recordEvent(load, "PICKUP_CONFIRMED", load.getCarrier(), pickupNotes, load.getCurrentLocation());
        notificationService.notifyPickupConfirmed(load);

        return load;
    }

    // ════════════════════════════════════════════════════════
    // ACTION 4: Carrier updates location (called by carrier app or bot)
    // ════════════════════════════════════════════════════════
    @Transactional
    public Load updateLocation(String loadNumber, Long carrierId,
                                String location, String eta) {
        Load load = findByNumber(loadNumber);
        validateCarrierOwnership(load, carrierId);

        load.setStatus(LoadStatus.IN_TRANSIT);
        load.setCurrentLocation(location);
        load = loadRepository.save(load);

        recordEvent(load, "LOCATION_UPDATE", load.getCarrier(), "Location: " + location, location);
        notificationService.sendLocationUpdate(load, location, eta);

        return load;
    }

    // ════════════════════════════════════════════════════════
    // ACTION 5: Carrier marks out for delivery
    // ════════════════════════════════════════════════════════
    @Transactional
    public Load markOutForDelivery(String loadNumber, Long carrierId) {
        Load load = findByNumber(loadNumber);
        validateCarrierOwnership(load, carrierId);
        validateTransition(load, LoadStatus.OUT_FOR_DELIVERY);

        load.setStatus(LoadStatus.OUT_FOR_DELIVERY);
        load = loadRepository.save(load);

        recordEvent(load, "OUT_FOR_DELIVERY", load.getCarrier(), "Out for delivery", null);
        notificationService.notifyOutForDelivery(load);  // Sends buttons to receiver

        return load;
    }

    // ════════════════════════════════════════════════════════
    // ACTION 6: Carrier confirms delivery
    // ════════════════════════════════════════════════════════
    @Transactional
    public Load confirmDelivery(String loadNumber, Long carrierId, String deliveryNotes) {
        Load load = findByNumber(loadNumber);
        validateCarrierOwnership(load, carrierId);
        validateTransition(load, LoadStatus.DELIVERED);

        load.setStatus(LoadStatus.DELIVERED);
        load.setActualDelivery(LocalDateTime.now());
        load = loadRepository.save(load);

        recordEvent(load, "DELIVERED", load.getCarrier(), deliveryNotes, null);
        notificationService.notifyDeliveryConfirmed(load);

        return load;
    }

    // ════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ════════════════════════════════════════════════════════
    private void validateTransition(Load load, LoadStatus newStatus) {
        boolean valid = switch (newStatus) {
            case CARRIER_ACCEPTED   -> load.getStatus() == LoadStatus.POSTED;
            case PICKUP_CONFIRMED   -> load.getStatus() == LoadStatus.CARRIER_ACCEPTED;
            case IN_TRANSIT         -> load.getStatus() == LoadStatus.PICKUP_CONFIRMED || load.getStatus() == LoadStatus.IN_TRANSIT;
            case OUT_FOR_DELIVERY   -> load.getStatus() == LoadStatus.IN_TRANSIT;
            case DELIVERED          -> load.getStatus() == LoadStatus.OUT_FOR_DELIVERY;
            case DELIVERY_FAILED    -> load.getStatus() == LoadStatus.DELIVERY_FAILED;
            case DELAYED            -> load.getStatus() == LoadStatus.IN_TRANSIT || load.getStatus() == LoadStatus.PICKUP_CONFIRMED;
            case CANCELLED          -> load.getStatus() == LoadStatus.POSTED || load.getStatus() == LoadStatus.CARRIER_ACCEPTED;
            default -> false;
        };
        if (!valid) {
            throw new IllegalStateException(
                "Cannot transition load " + load.getLoadNumber() +
                " from " + load.getStatus() + " to " + newStatus
            );
        }
    }

    private void validateCarrierOwnership(Load load, Long carrierId) {
        if (load.getCarrier() == null || !load.getCarrier().getId().equals(carrierId)) {
            throw new SecurityException("Carrier " + carrierId + " is not assigned to this load");
        }
    }

    private Load findByNumber(String loadNumber) {
        return loadRepository.findByLoadNumber(loadNumber)
        		.orElseThrow(() -> new RuntimeException("Load not found: " + loadNumber));
    }

    private void recordEvent(Load load, String type, User triggeredBy,
                              String description, String location) {
        LoadEvent event = new LoadEvent();
        event.setLoad(load);
        event.setEventType(type);
        event.setTriggeredBy(triggeredBy);
        event.setDescription(description);
        event.setLocation(location);
        eventRepository.save(event);
    }

    private String generateLoadNumber() {
        String date = LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = loadRepository.count() + 1;
        return String.format("SHP-%s-%04d", date, count);
    }

}
