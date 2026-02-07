package com.example.airbnbbookingspring.services.status;

import com.example.airbnbbookingspring.models.Booking;
import com.example.airbnbbookingspring.dtos.UpdateBookingRequest;

public interface BookingStatusUpdateHandler {
    boolean supports(UpdateBookingRequest request, Booking booking);

    void handle(UpdateBookingRequest request, Booking booking);
}
