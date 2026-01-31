package com.example.airbnbbookingspring.services.concurrency;


import java.time.LocalDate;
import java.util.List;

import com.example.airbnbbookingspring.models.Availability;

public interface ConcurrencyControlStrategy {

    void releaseLock(Long airbnbId, LocalDate checkInDate, LocalDate checkOutDate);

    List<Availability>lockAndCheckAvailability(Long airbnbId, LocalDate checkInDate, LocalDate checkOutDate);
}