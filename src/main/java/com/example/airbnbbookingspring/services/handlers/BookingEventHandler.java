package com.example.airbnbbookingspring.services.handlers;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import com.example.airbnbbookingspring.models.BookingStatus;
import com.example.airbnbbookingspring.repositories.reads.RedisWriteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.example.airbnbbookingspring.models.Booking;
import com.example.airbnbbookingspring.repositories.writes.BookingWriteRepository;
import com.example.airbnbbookingspring.saga.SagaEvent;
import com.example.airbnbbookingspring.saga.SagaEventPublisher;

import lombok.RequiredArgsConstructor;

/**
 * Handles booking confirmation and cancellation events, updates booking status,
 * and manages compensation events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingEventHandler {

    private final BookingWriteRepository bookingWriteRepository;
    private final SagaEventPublisher sagaEventPublisher;
    private final RedisWriteRepository redisWriteRepository;

    @Value("${airbnb.saga.compensation-event:BOOKING_COMPENSATED}")
    private String compensationEventType;

    /**
     * Handles booking confirmation event. Updates booking status and publishes
     * compensation event if needed.
     * 
     * @param sagaEvent Saga event containing booking details
     */
    @Transactional
    public void handleBookingConfirmRequested(SagaEvent sagaEvent) {
        Map<String, Object> payload = sagaEvent.getPayload();
        try {
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            Long airbnbId = Long.valueOf(payload.get("airbnbId").toString());
            LocalDate checkInDate = LocalDate.parse(payload.get("checkInDate").toString());
            LocalDate checkOutDate = LocalDate.parse(payload.get("checkOutDate").toString());

            Booking booking = bookingWriteRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingWriteRepository.save(booking);
            redisWriteRepository.writeBookingReadModel(booking);

            sagaEventPublisher.publishEvent("BOOKING_CONFIRMED", "CONFIRM_BOOKING",
                    Map.of("bookingId", bookingId, "airbnbId", airbnbId, "checkInDate", checkInDate.toString(),
                            "checkOutDate", checkOutDate.toString()));
        } catch (IllegalArgumentException e) {
            log.error("Booking not found: {}", e.getMessage(), e);
            publishCompensationEvent(payload);
            throw e;
        } catch (Exception e) {
            log.error("Failed to confirm booking: {}", e.getMessage(), e);
            publishCompensationEvent(payload);
            throw new RuntimeException("Failed to confirm booking", e);
        }
    }

    /**
     * Handles booking cancellation event. Updates booking status and publishes
     * compensation event if needed.
     * 
     * @param sagaEvent Saga event containing booking details
     */
    @Transactional
    public void handleBookingCancelRequested(SagaEvent sagaEvent) {
        Map<String, Object> payload = sagaEvent.getPayload();
        try {
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            Long airbnbId = Long.valueOf(payload.get("airbnbId").toString());
            LocalDate checkInDate = LocalDate.parse(payload.get("checkInDate").toString());
            LocalDate checkOutDate = LocalDate.parse(payload.get("checkOutDate").toString());

            Booking booking = bookingWriteRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingWriteRepository.save(booking);
            redisWriteRepository.writeBookingReadModel(booking);

            sagaEventPublisher.publishEvent("BOOKING_CANCELLED", "CANCEL_BOOKING",
                    Map.of("bookingId", bookingId, "airbnbId", airbnbId, "checkInDate", checkInDate.toString(),
                            "checkOutDate", checkOutDate.toString()));
        } catch (IllegalArgumentException e) {
            log.error("Booking not found: {}", e.getMessage(), e);
            publishCompensationEvent(payload);
            throw e;
        } catch (Exception e) {
            log.error("Failed to cancel booking: {}", e.getMessage(), e);
            publishCompensationEvent(payload);
            throw new RuntimeException("Failed to cancel booking", e);
        }
    }

    /**
     * Publishes a compensation event for failed booking operations.
     * 
     * @param payload Saga event payload
     */
    private void publishCompensationEvent(Map<String, Object> payload) {
        Object bookingIdObj = payload.get("bookingId");
        BookingStatus bookingStatus = null;
        if (bookingIdObj != null) {
            try {
                Long bookingId = Long.valueOf(bookingIdObj.toString());
                Booking booking = bookingWriteRepository.findById(bookingId).orElse(null);
                if (booking != null) {
                    bookingStatus = booking.getBookingStatus();
                }
            } catch (Exception ignored) {
            }
        }
        Map<String, Object> compensationPayload = new java.util.HashMap<>(payload);
        compensationPayload.put("currentBookingStatus", bookingStatus != null ? bookingStatus.name() : null);
        sagaEventPublisher.publishEvent(compensationEventType, "COMPENSATE_BOOKING", compensationPayload);
    }
}