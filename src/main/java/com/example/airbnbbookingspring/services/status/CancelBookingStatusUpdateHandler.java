package com.example.airbnbbookingspring.services.status;

import com.example.airbnbbookingspring.dtos.UpdateBookingRequest;
import com.example.airbnbbookingspring.models.Booking;
import com.example.airbnbbookingspring.models.BookingStatus;
import com.example.airbnbbookingspring.saga.SagaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CancelBookingStatusUpdateHandler implements BookingStatusUpdateHandler {
    private final SagaEventPublisher sagaEventPublisher;

    @Override
    public boolean supports(UpdateBookingRequest request, Booking booking) {
        return request.getBookingStatus() == BookingStatus.CANCELLED;
    }

    @Override
    public void handle(UpdateBookingRequest request, Booking booking) {
        sagaEventPublisher.publishEvent(
                "BOOKING_CANCEL_REQUESTED",
                "CANCEL_BOOKING",
                Map.of(
                        "bookingId", booking.getId(),
                        "airbnbId", booking.getAirbnb() != null ? booking.getAirbnb().getId() : null,
                        "checkInDate", booking.getCheckInDate(),
                        "checkOutDate", booking.getCheckOutDate()));
    }
}
