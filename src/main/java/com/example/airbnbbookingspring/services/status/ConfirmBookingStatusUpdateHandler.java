package com.example.airbnbbookingspring.services.status;

import com.example.airbnbbookingspring.dtos.UpdateBookingRequest;
import com.example.airbnbbookingspring.models.Booking;
import com.example.airbnbbookingspring.models.BookingStatus;
import com.example.airbnbbookingspring.saga.SagaEventSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ConfirmBookingStatusUpdateHandler implements BookingStatusUpdateHandler {
    private final SagaEventSender sagaEventSender;

    @Override
    public boolean supports(UpdateBookingRequest request, Booking booking) {
        return request.getBookingStatus() == BookingStatus.CONFIRMED;
    }

    @Override
    public void handle(UpdateBookingRequest request, Booking booking) {
        // Compose event JSON manually or via a utility
        String eventJson = String.format(
                "{\"bookingId\":%d,\"airbnbId\":%d,\"checkInDate\":\"%s\",\"checkOutDate\":\"%s\",\"eventType\":\"BOOKING_CONFIRM_REQUESTED\",\"step\":\"CONFIRM_BOOKING\"}",
                booking.getId(),
                booking.getAirbnb() != null ? booking.getAirbnb().getId() : null,
                booking.getCheckInDate(),
                booking.getCheckOutDate());
        sagaEventSender.send(eventJson);
    }
}
