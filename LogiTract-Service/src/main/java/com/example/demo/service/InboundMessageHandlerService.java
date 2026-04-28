package com.example.demo.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ConversationState;
import com.example.demo.entity.Load;
import com.example.demo.entity.User;
import com.example.demo.repository.LoadRepository;
import com.example.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor

public class InboundMessageHandlerService {

    private final ChatigoService           chatigoService;
    private final LoadService              loadService;
    private final ConversationStateService stateService;
    private final UserRepository           userRepository;
    private final LoadRepository           loadRepository;
    
	public InboundMessageHandlerService(ChatigoService chatigoService, 
			LoadService loadService, ConversationStateService stateService, 
			UserRepository userRepository, LoadRepository loadRepository) {
		super();
		this.chatigoService = chatigoService;
		this.loadService = loadService;
		this.stateService = stateService;
		this.userRepository = userRepository;
		this.loadRepository = loadRepository;
	}
 
	private static final Logger log=LoggerFactory.getLogger(InboundMessageHandlerService.class);
	
    // ════════════════════════════════════════════════════════
    // MAIN ENTRY POINT — called by WebhookController
    // ════════════════════════════════════════════════════════
    public void handleIncomingText(String fromPhone, String messageBody) {
        log.info("Inbound from {}: {}", fromPhone, messageBody);

        // 1. Find user by phone number
        Optional<User> userOpt = userRepository.findByPhone(fromPhone);
        if (userOpt.isEmpty()) {
            chatigoService.sendText(fromPhone,
                "Welcome to LogiTrack! Your number is not registered. " +
                "Please contact your account manager.");
            return;
        }

        User user = userOpt.get();
        String msg = messageBody.trim();
        String msgUpper = msg.toUpperCase();

        // 2. Route based on user role
        switch (user.getRole()) {
            case CARRIER  -> handleCarrierMessage(user, msg, msgUpper);
            case SHIPPER  -> handleShipperMessage(user, msg, msgUpper);
            case RECEIVER -> handleReceiverMessage(user, msg, msgUpper);
        }
    }

    // ════════════════════════════════════════════════════════
    // Handle Button Reply (when user taps an interactive button)
    // ════════════════════════════════════════════════════════
    public void handleButtonReply(String fromPhone, String buttonId, String buttonTitle) {
        log.info("Button tapped by {}: id={}", fromPhone, buttonId);

        Optional<User> userOpt = userRepository.findByPhone(fromPhone);
        if (userOpt.isEmpty()) return;
        User user = userOpt.get();

        Optional<ConversationState> stateOpt = stateService.getState(fromPhone);

        switch (buttonId) {
            case "confirm_accept" -> {
                // Carrier tapped "Confirm Accept"
                String loadNumber = stateOpt.map(ConversationState::getLoadNumber).orElse(null);
                if (loadNumber != null) {
                    loadService.acceptLoad(loadNumber, user.getId());
                    stateService.clearState(fromPhone);
                    chatigoService.sendText(fromPhone,
                        "You have accepted load " + loadNumber + ". " +
                        "Report to pickup location and confirm when goods are loaded. " +
                        "Reply: PICKUP " + loadNumber + " when ready.");
                }
            }
            case "decline_load" -> {
                stateService.clearState(fromPhone);
                chatigoService.sendText(fromPhone,
                    "Load declined. You will be notified of other available loads.");
            }
            case "confirm_ready" -> {
                // Receiver tapped "I am Ready" for delivery
                String loadNumber = stateOpt.map(ConversationState::getLoadNumber).orElse(null);
                chatigoService.sendText(fromPhone,
                    "Great! The driver has been notified that you are ready. " +
                    "Estimated arrival: within 2 hours.");
            }
            case "reschedule" -> {
                chatigoService.sendText(fromPhone,
                    "To reschedule your delivery, please call our support: 1800-XXX-XXXX " +
                    "or reply with your preferred delivery date (DD/MM/YYYY).");
            }
            default -> log.warn("Unknown button id: {}", buttonId);
        }
    }

    // ════════════════════════════════════════════════════════
    // CARRIER MESSAGE HANDLERS
    // ════════════════════════════════════════════════════════
    private void handleCarrierMessage(User carrier, String msg, String msgUpper) {
        // Command: DETAILS <LOAD_NUMBER> — show load details
        if (msgUpper.startsWith("DETAILS ")) {
            String loadNumber = msg.substring(8).trim().toUpperCase();
            showLoadDetailsToCarrier(carrier, loadNumber);

        // Command: PICKUP <LOAD_NUMBER> — confirm goods picked up
        } else if (msgUpper.startsWith("PICKUP ")) {
            String loadNumber = msg.substring(7).trim().toUpperCase();
            loadService.confirmPickup(loadNumber, carrier.getId(), "Confirmed via WhatsApp");
            stateService.setState(carrier.getPhone(),
                ConversationStateService.AWAITING_LOCATION, loadNumber, null);
            chatigoService.sendText(carrier.getPhone(),
                "Pickup confirmed for " + loadNumber + ". " +
                "Reply LOC <location> anytime to update your position. " +
                "Example: LOC Bhopal Bypass");

        // Command: LOC <location> — send location update
        } else if (msgUpper.startsWith("LOC ")) {
            String location = msg.substring(4).trim();
            handleLocationUpdate(carrier, location);

        // Command: DELIVER <LOAD_NUMBER> — mark delivered
        } else if (msgUpper.startsWith("DELIVER ")) {
            String loadNumber = msg.substring(8).trim().toUpperCase();
            loadService.confirmDelivery(loadNumber, carrier.getId(), "Confirmed via WhatsApp");
            stateService.clearState(carrier.getPhone());
            chatigoService.sendText(carrier.getPhone(),
            		"Delivery confirmed for " + loadNumber + ". Thank you!");

            // Command: HELP — show available commands
            } else if (msgUpper.equals("HELP")) {
                chatigoService.sendText(carrier.getPhone(), getCarrierHelp());

            } else {
                chatigoService.sendText(carrier.getPhone(),
                    "Unknown command. Reply HELP to see available commands.");
            }
        }

        // ════════════════════════════════════════════════════════
        // SHIPPER MESSAGE HANDLERS
        // ════════════════════════════════════════════════════════
        private void handleShipperMessage(User shipper, String msg, String msgUpper) {
            if (msgUpper.startsWith("TRACK ")) {
                String loadNumber = msg.substring(6).trim().toUpperCase();
                sendTrackingInfo(shipper, loadNumber);
            } else if (msgUpper.equals("MYLOADS")) {
                sendActiveLoads(shipper);
            } else if (msgUpper.equals("HELP")) {
                chatigoService.sendText(shipper.getPhone(), getShipperHelp());
            } else {
                chatigoService.sendText(shipper.getPhone(),
                    "Reply TRACK <load#> to track a shipment, or MYLOADS to see all active loads.");
            }
        }

        // ════════════════════════════════════════════════════════
        // RECEIVER MESSAGE HANDLERS
        // ════════════════════════════════════════════════════════
        private void handleReceiverMessage(User receiver, String msg, String msgUpper) {
            if (msgUpper.startsWith("TRACK ")) {
                String loadNumber = msg.substring(6).trim().toUpperCase();
                sendTrackingInfo(receiver, loadNumber);
            } else if (msgUpper.startsWith("RATE ")) {
                // Rating flow: RATE 5 or RATE <load#> 5
                chatigoService.sendText(receiver.getPhone(),
                    "Thank you for your rating! We appreciate your feedback.");
            } else {
                chatigoService.sendText(receiver.getPhone(),
                    "Reply TRACK <load#> to get status of your delivery.");
            }
        }

        // ════════════════════════════════════════════════════════
        // HELPERS
        // ════════════════════════════════════════════════════════
        private void showLoadDetailsToCarrier(User carrier, String loadNumber) {
            loadRepository.findByLoadNumber(loadNumber).ifPresentOrElse(
                load -> {
                    stateService.setState(carrier.getPhone(),
                        ConversationStateService.LOAD_DETAILS_SHOWN, loadNumber, null);
                    chatigoService.sendButtons(
                            carrier.getPhone(),
                            String.format(
                                "Load #%s%nRoute: %s to %s%nCargo: %s | Weight: %s Tons%nPrice: Rs. %s%nPickup: %s",
                                load.getLoadNumber(), load.getOriginCity(), load.getDestinationCity(),
                                load.getCargoType(), load.getWeightTons(),
                                load.getQuotedPrice(),
                                load.getScheduledPickup() != null ? load.getScheduledPickup().toString() : "TBD"
                            ),
                            "Available Load",
                            List.of(
                                new String[]{"confirm_accept", "Accept Load"},
                                new String[]{"decline_load",   "Decline"}
                            )
                        );
                    },
                    () -> chatigoService.sendText(carrier.getPhone(),
                        "Load " + loadNumber + " not found. Check the load number and try again.")
                );
            }

            private void handleLocationUpdate(User carrier, String location) {
                stateService.getState(carrier.getPhone()).ifPresent(state -> {
                    if (state.getLoadNumber() != null) {
                        loadService.updateLocation(
                            state.getLoadNumber(), carrier.getId(),
                            location, "ETA will be updated shortly"
                        );
                        chatigoService.sendText(carrier.getPhone(),
                            "Location updated: " + location +
                            ". Shipper and receiver have been notified.");
                    }
                });
            }

            private void sendTrackingInfo(User user, String loadNumber) {
                loadRepository.findByLoadNumber(loadNumber).ifPresentOrElse(
                    load -> chatigoService.sendText(user.getPhone(),
                        String.format(
                            "Tracking: Shipment #%s%nStatus: %s%nCurrent Location: %s%nEstimated Delivery: %s",
                            load.getLoadNumber(),
                            load.getStatus().name().replace("_"," "),
                            load.getCurrentLocation() != null ? load.getCurrentLocation() : "En route",
                            load.getEstimatedDelivery() != null ? load.getEstimatedDelivery().toString() : "TBD"
                        )
                    ),
                    () -> chatigoService.sendText(user.getPhone(), "Load " + loadNumber + " not found.")
                );
            }
            private void sendActiveLoads(User shipper) {
                List<Load> loads = loadRepository.findByShipperAndStatusNotIn(
                    shipper, List.of(Load.LoadStatus.DELIVERED, Load.LoadStatus.CANCELLED)
                );
                if (loads.isEmpty()) {
                    chatigoService.sendText(shipper.getPhone(), "You have no active loads.");
                    return;
                }
                StringBuilder sb = new StringBuilder("Your Active Loads:\n");
                loads.forEach(l -> sb.append(String.format(
                    "%s: %s to %s | Status: %s\n",
                    l.getLoadNumber(), l.getOriginCity(), l.getDestinationCity(),
                    l.getStatus().name().replace("_"," ")
                )));
                chatigoService.sendText(shipper.getPhone(), sb.toString().trim());
            }

            private String getCarrierHelp() {
                return "LogiTrack Carrier Commands:\n" +
                       "DETAILS <LOAD#>  — View load details\n" +
                       "PICKUP <LOAD#>   — Confirm goods picked up\n" +
                       "LOC <location>   — Send location update\n" +
                       "DELIVER <LOAD#>  — Confirm delivery complete\n" +
                       "HELP             — Show this menu";
            }

            private String getShipperHelp() {
                return "LogiTrack Shipper Commands:\n" +
                       "TRACK <LOAD#>  — Get live tracking status\n" +
                       "MYLOADS        — View all active loads\n" +
                       "HELP           — Show this menu";
            }

    
    
    
}
