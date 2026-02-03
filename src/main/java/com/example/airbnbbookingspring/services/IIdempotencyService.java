package com.example.airbnbbookingspring.services;


import java.util.Optional;

import com.example.airbnbbookingspring.models.Booking;

public interface IIdempotencyService {

    boolean isIdempotencyKeyUsed(String idempotencyKey);

    Optional<Booking> findBookingByIdempotencyKey(String idempotencyKey);
}