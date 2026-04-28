package com.example.demo.chatigo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Load;
import com.example.demo.repository.LoadRepository;
import com.example.demo.service.NotificationService;

import java.util.List;

// @Component makes this a Spring-managed bean.
// @Scheduled methods run automatically at the configured intervals.
// Add @EnableScheduling to your main class or a @Configuration class.
@Component
@Slf4j
@RequiredArgsConstructor
public class LocationUpdateScheduler {
	private final LoadRepository      loadRepository;
    private final NotificationService notificationService;

    public LocationUpdateScheduler(LoadRepository loadRepository, NotificationService notificationService) {
		super();
		this.loadRepository = loadRepository;
		this.notificationService = notificationService;
	}

    private static final Logger log=LoggerFactory.getLogger(LocationUpdateScheduler.class);
    
	// Run every 4 hours — send location update to Shipper and Receiver
    // Cron: "0 0 */4 * * *" = at minute 0 of every 4th hour
    @Scheduled(cron = "0 0 */4 * * *")
    public void sendPeriodicLocationUpdates() {
        log.info("Running scheduled location update job");

        // Find all loads currently in transit
        List<Load> activeLoads = loadRepository.findByStatusIn(
            List.of(Load.LoadStatus.IN_TRANSIT, Load.LoadStatus.PICKUP_CONFIRMED)
        );
        for (Load load : activeLoads) {
            if (load.getCurrentLocation() == null) continue;

            // In production: call GPS API to get real current location
            // String realLocation = gpsService.getCurrentLocation(load.getCarrier().getVehicleId());
            String location = load.getCurrentLocation(); // Use last known for now
            String eta = calculateEta(load);

            notificationService.sendLocationUpdate(load, location, eta);
            log.info("Location update sent for load: {}", load.getLoadNumber());
        }
    }

    // Check for overdue loads every hour
    @Scheduled(cron = "0 0 * * * *")
    public void checkForDelays() {
        List<Load> overdueLoads = loadRepository.findOverdueLoads(
            java.time.LocalDateTime.now()
        );
        for (Load load : overdueLoads) {
            if (load.getStatus() == Load.LoadStatus.DELAYED) continue; // Already flagged
            log.warn("Load {} is overdue. Sending delay alert.", load.getLoadNumber());
            notificationService.notifyDelay(load, "Traffic / road conditions", "Calculating new ETA");
        }
    }

    private String calculateEta(Load load) {
        if (load.getEstimatedDelivery() == null) return "To be confirmed";
        return load.getEstimatedDelivery()
            .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM, hh:mm a"));
    }

}
