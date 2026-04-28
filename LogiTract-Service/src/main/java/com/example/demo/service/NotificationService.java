package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Load;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.service.ChatigoService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor

public class NotificationService {
	private final ChatigoService chatigoService;
    private final NotificationRepository notificationRepository;
  
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
      
    public NotificationService(ChatigoService chatigoService, NotificationRepository notificationRepository) {
        this.chatigoService = chatigoService;
        this.notificationRepository = notificationRepository;
    }
    
    
    
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    // ════════════════════════════════════════════════════════
    // EVENT 1: New load posted — notify matching Carriers
    // ════════════════════════════════════════════════════════
    public void notifyCarriersOfNewLoad(Load load, List<User> carriers) {
        log.info("Notifying {} carriers of load: {}", carriers.size(), load.getLoadNumber());

        for (User carrier : carriers) {
            ChatigoService.ChatigoResponse resp = chatigoService.sendTemplate(
                carrier.getPhone(),
                "load_posted_carrier",
                List.of(
                    load.getLoadNumber(),
                    load.getOriginCity(),
                    load.getDestinationCity(),
                    load.getWeightTons().toPlainString(),
                    load.getQuotedPrice().toPlainString()
                )
            );
            saveNotification(load, carrier, "load_posted_carrier",
                "Template: load_posted_carrier", resp);
        }
    }

    // ════════════════════════════════════════════════════════
    // EVENT 2: Carrier accepted — notify Shipper AND Receiver
    // ════════════════════════════════════════════════════════
    public void notifyCarrierAccepted(Load load) {
        User carrier  = load.getCarrier();
        User shipper  = load.getShipper();
        User receiver = load.getReceiver();

        // ─── Notify Shipper ───
        String shipperMsg = String.format(
            "Your shipment %s has been accepted by %s (Contact: %s). " +
            "Pickup scheduled for: %s",
            load.getLoadNumber(),
            carrier.getCompanyName(),
            carrier.getPhone(),
            load.getScheduledPickup() != null ? load.getScheduledPickup().format(FMT) : "TBD"
        );
        ChatigoService.ChatigoResponse r1 = chatigoService.sendText(shipper.getPhone(), shipperMsg);
        saveNotification(load, shipper, "CARRIER_ACCEPTED", shipperMsg, r1);

        // ─── Notify Receiver ───
        String receiverMsg = String.format(
            "Your delivery from %s is on the way! Shipment %s. " +
            		"Expected arrival: %s. Reply TRACK %s for live updates.",
                    load.getOriginCity(),
                    load.getLoadNumber(),
                    load.getEstimatedDelivery() != null ? load.getEstimatedDelivery().format(FMT) : "TBD",
                    load.getLoadNumber()
                );
                ChatigoService.ChatigoResponse r2 = chatigoService.sendText(receiver.getPhone(), receiverMsg);
                saveNotification(load, receiver, "CARRIER_ACCEPTED", receiverMsg, r2);
            }

            // ════════════════════════════════════════════════════════
            // EVENT 3: Goods picked up
            // ════════════════════════════════════════════════════════
            public void notifyPickupConfirmed(Load load) {
                String pickupTime = LocalDateTime.now().format(FMT);
                String carrierName = load.getCarrier().getCompanyName();

                // Shipper notification
                ChatigoService.ChatigoResponse r1 = chatigoService.sendTemplate(
                    load.getShipper().getPhone(),
                    "pickup_confirmed",
                    List.of(load.getLoadNumber(), pickupTime, carrierName)
                );
                saveNotification(load, load.getShipper(), "PICKUP_CONFIRMED",
                                 "Template: pickup_confirmed", r1);

                // Receiver notification (same template, same params)
                ChatigoService.ChatigoResponse r2 = chatigoService.sendTemplate(
                    load.getReceiver().getPhone(),
                    "pickup_confirmed",
                    List.of(load.getLoadNumber(), pickupTime, carrierName)
                );
                saveNotification(load, load.getReceiver(), "PICKUP_CONFIRMED",
                                 "Template: pickup_confirmed", r2);
            }

            // ════════════════════════════════════════════════════════
            // EVENT 4: Location update (called by scheduled job)
            // ════════════════════════════════════════════════════════
            public void sendLocationUpdate(Load load, String currentLocation, String eta) {
                List<User> recipients = List.of(load.getShipper(), load.getReceiver());

                for (User recipient : recipients) {
                    ChatigoService.ChatigoResponse resp = chatigoService.sendTemplate(
                        recipient.getPhone(),
                        "location_update",
                        List.of(load.getLoadNumber(), currentLocation, eta)
                    );
                    saveNotification(load, recipient, "LOCATION_UPDATE",
                        "Location: " + currentLocation, resp);
                }
            }
         // EVENT 5: Delay alert
            // ════════════════════════════════════════════════════════
            public void notifyDelay(Load load, String reason, String newEta) {
                List<User> recipients = List.of(load.getShipper(), load.getReceiver());

                for (User recipient : recipients) {
                    ChatigoService.ChatigoResponse resp = chatigoService.sendTemplate(
                        recipient.getPhone(),
                        "delay_alert",
                        List.of(load.getLoadNumber(), reason, newEta)
                    );
                    saveNotification(load, recipient, "DELAY_ALERT",
                        "Delay: " + reason, resp);
                }
            }

            // ════════════════════════════════════════════════════════
            // EVENT 6: Out for delivery — Receiver only
            // ════════════════════════════════════════════════════════
            public void notifyOutForDelivery(Load load) {
                User carrier  = load.getCarrier();
                User receiver = load.getReceiver();

                ChatigoService.ChatigoResponse resp = chatigoService.sendButtons(
                    receiver.getPhone(),
                    String.format("Your delivery is arriving today! Driver: %s, Phone: %s",
                        carrier.getName(), carrier.getPhone()),
                    "Shipment #" + load.getLoadNumber(),
                    List.of(
                        new String[]{"confirm_ready", "I am Ready"},
                        new String[]{"reschedule",    "Reschedule"},
                        new String[]{"call_driver",   "Call Driver"}
                    )
                );
                saveNotification(load, receiver, "OUT_FOR_DELIVERY",
                    "Out for delivery notification", resp);
            }

            // ════════════════════════════════════════════════════════
            // EVENT 7: Delivery confirmed
            // ════════════════════════════════════════════════════════
            public void notifyDeliveryConfirmed(Load load) {
                String deliveryTime = LocalDateTime.now().format(FMT);
                String receiverName = load.getReceiver().getName();

                for (User recipient : List.of(load.getShipper(), load.getReceiver())) {
                    ChatigoService.ChatigoResponse resp = chatigoService.sendTemplate(
                        recipient.getPhone(),
                        "delivery_confirmed",
                        List.of(load.getLoadNumber(), deliveryTime, receiverName)
                    );
                    saveNotification(load, recipient, "DELIVERED",
                        "Template: delivery_confirmed", resp);
                }
            }
            // ════════════════════════════════════════════════════════
            // PRIVATE: Save notification record to DB
            // ════════════════════════════════════════════════════════
            private void saveNotification(Load load, User recipient,
                                           String eventType, String messageText,
                                           ChatigoService.ChatigoResponse resp) {
                Notification notification = new Notification();
                notification.setLoad(load);
                notification.setRecipient(recipient);
                notification.setRecipientPhone(recipient.getPhone());
                notification.setRecipientRole(recipient.getRole().name());
                notification.setMessageText(messageText);
                notification.setStatus(resp.success() ? "SENT" : "FAILED");
                notification.setErrorMessage(resp.success() ? null : resp.error());
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
            }

}
