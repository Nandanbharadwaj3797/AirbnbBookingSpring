package com.example.airbnbbookingspring.services.handlers;


import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.example.airbnbbookingspring.repositories.writes.AvailabilityWriteRepository;
import com.example.airbnbbookingspring.saga.SagaEvent;
import com.example.airbnbbookingspring.saga.SagaEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityEventHandler {

    private final AvailabilityWriteRepository availabilityWriteRepository;
    private final SagaEventPublisher sagaEventPublisher;

    @Value("${airbnb.saga.compensation-event:BOOKING_COMPENSATED}")
    private String compensationEventType;

    /**
     * Handles booking confirmation event. Updates availability and publishes compensation event if needed.
     * @param sagaEvent Saga event containing booking details
     */
    @Transactional
    public void handleBookingConfirmed(SagaEvent sagaEvent) {
        Map<String, Object> payload = sagaEvent.getPayload();
        try {
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            Long airbnbId = Long.valueOf(payload.get("airbnbId").toString());
            LocalDate checkInDate = LocalDate.parse(payload.get("checkInDate").toString());
            LocalDate checkOutDate = LocalDate.parse(payload.get("checkOutDate").toString());

            Long count = availabilityWriteRepository.countByAirbnbIdAndDateBetweenAndBookingIdIsNotNull(airbnbId, checkInDate, checkOutDate);
            log.info("Count of bookings for Airbnb {}: {}", airbnbId, count);
            if(count > 0) {
                sagaEventPublisher.publishEvent("BOOKING_CANCEL_REQUESTED", "CANCEL_BOOKING", payload);
                log.error("Airbnb is not available for the given dates. BookingId: {}, AirbnbId: {}", bookingId, airbnbId);
                throw new IllegalStateException("Airbnb is not available for the given dates. Please try again with different dates.");
            }
            log.info("Updating availability for Airbnb {}: {}", airbnbId, checkInDate, checkOutDate);
            availabilityWriteRepository.updateBookingIdByAirbnbIdAndDateBetween(bookingId, airbnbId, checkInDate, checkOutDate);
            log.info("Availability updated for Airbnb {}: {}", airbnbId, checkInDate, checkOutDate);
        } catch (IllegalStateException e) {
            publishCompensationEvent(payload);
            throw e;
        } catch (Exception e) {
            log.error("Failed to confirm booking: {}", e.getMessage(), e);
            publishCompensationEvent(payload);
            throw new RuntimeException("Failed to confirm booking", e);
        }
    }

    /**
     * Handles booking cancellation event. Updates availability and publishes compensation event if needed.
     * @param sagaEvent Saga event containing booking details
     */
    @Transactional
    public void handleBookingCancelled(SagaEvent sagaEvent) {
        Map<String, Object> payload = sagaEvent.getPayload();
        try {
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            Long airbnbId = Long.valueOf(payload.get("airbnbId").toString());
            LocalDate checkInDate = LocalDate.parse(payload.get("checkInDate").toString());
            LocalDate checkOutDate = LocalDate.parse(payload.get("checkOutDate").toString());

            availabilityWriteRepository.updateBookingIdByAirbnbIdAndDateBetween(null, airbnbId, checkInDate, checkOutDate);
        } catch (Exception e) {
            log.error("Failed to cancel booking: {}", e.getMessage(), e);
            publishCompensationEvent(payload);
            throw new RuntimeException("Failed to cancel booking", e);
        }
    }

    /**
     * Publishes a compensation event for failed booking operations.
     * @param payload Saga event payload
     */
    private void publishCompensationEvent(Map<String, Object> payload) {
        sagaEventPublisher.publishEvent(compensationEventType, "COMPENSATE_BOOKING", payload);
    }
}