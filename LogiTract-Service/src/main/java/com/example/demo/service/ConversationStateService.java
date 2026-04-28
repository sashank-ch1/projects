package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ConversationState;
import com.example.demo.repository.ConversationStateRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor

public class ConversationStateService {
	private final ConversationStateRepository stateRepository;

	private static final Logger log=LoggerFactory.getLogger(ConversationStateService.class);
	
	public ConversationStateService(ConversationStateRepository stateRepository) {
		this.stateRepository=stateRepository;
	}
	
    // Get current state for a user (by phone)
    public Optional<ConversationState> getState(String phone) {
        return stateRepository.findByUserPhone(phone);
    }

    // Set state — what step is this user currently on?
    public void setState(String phone, String step,
                          String loadNumber, String contextJson) {
        ConversationState state = stateRepository
        		.findByUserPhone(phone)
                .orElse(new ConversationState());

            state.setUserPhone(phone);
            state.setCurrentStep(step);
            state.setLoadNumber(loadNumber);
            state.setContextData(contextJson);
            stateRepository.save(state);
            log.info("State set for {}: step={}, load={}", phone, step, loadNumber);
        }

        // Clear state after a flow completes
        public void clearState(String phone) {
            stateRepository.findByUserPhone(phone)
                .ifPresent(stateRepository::delete);
            log.info("State cleared for: {}", phone);
        }

        // Step constants — all possible conversation steps
        public static final String IDLE                   = "IDLE";
        public static final String AWAITING_LOAD_DETAILS  = "AWAITING_LOAD_DETAILS";
        public static final String LOAD_DETAILS_SHOWN     = "LOAD_DETAILS_SHOWN";
        public static final String AWAITING_LOCATION      = "AWAITING_LOCATION";
        public static final String AWAITING_DELIVERY_CONF = "AWAITING_DELIVERY_CONF";

}
