package com.example.airbnbbookingspring.services;

import com.example.airbnbbookingspring.dtos.CreateBookingRequest;
import com.example.airbnbbookingspring.dtos.UpdateBookingRequest;
import com.example.airbnbbookingspring.models.Booking;

public interface IBookingService {

    Booking createBooking(CreateBookingRequest createBookingRequest);

    Booking updateBooking(UpdateBookingRequest updateBookingRequest);
}