package com.example.airbnbbookingspring.services;

import com.example.airbnbbookingspring.models.Availability;
import java.util.List;
import java.util.Optional;

public interface IAvailabilityService {
    List<Availability> getAllAvailabilities();

    Optional<Availability> getAvailabilityById(Long id);

    Availability createAvailability(Availability availability);

    Optional<Availability> updateAvailability(Long id, Availability availability);

    boolean deleteAvailability(Long id);

    /**
     * Scheduled task to create recurring availability records for all Airbnbs for
     * the next 30 days.
     */
    void createRecurringAvailability();
}
