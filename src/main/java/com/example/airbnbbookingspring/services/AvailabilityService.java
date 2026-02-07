package com.example.airbnbbookingspring.services;

import com.example.airbnbbookingspring.models.Availability;
import com.example.airbnbbookingspring.repositories.writes.AvailabilityWriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

@Service
public class AvailabilityService implements IAvailabilityService {

    @Autowired
    private AvailabilityWriteRepository availabilityWriteRepository;

    @Autowired
    private com.example.airbnbbookingspring.repositories.writes.AirbnbWriteRepository airbnbWriteRepository;

    @Override
    public List<Availability> getAllAvailabilities() {
        return availabilityWriteRepository.findAll();
    }

    @Override
    public Optional<Availability> getAvailabilityById(Long id) {
        return availabilityWriteRepository.findById(id);
    }

    @Override
    public Availability createAvailability(Availability availability) {
        return availabilityWriteRepository.save(availability);
    }

    @Override
    public Optional<Availability> updateAvailability(Long id, Availability availability) {
        return availabilityWriteRepository.findById(id)
                .map(existing -> {
                    availability.setId(id);
                    return availabilityWriteRepository.save(availability);
                });
    }

    @Override
    public boolean deleteAvailability(Long id) {
        if (availabilityWriteRepository.existsById(id)) {
            availabilityWriteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Scheduled task to create recurring availability records for all Airbnbs for
     * the next 30 days.
     * Runs every day at 2:00 AM.
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void createRecurringAvailability() {
        List<com.example.airbnbbookingspring.models.Airbnb> airbnbs = airbnbWriteRepository.findAll();
        for (com.example.airbnbbookingspring.models.Airbnb airbnb : airbnbs) {
            for (int i = 0; i < 30; i++) {
                LocalDate date = LocalDate.now().plusDays(i);
                if (!availabilityWriteRepository.existsByAirbnbIdAndDate(airbnb.getId(), date)) {
                    Availability availability = Availability.builder()
                            .airbnb(airbnb)
                            .date(date)
                            .build();
                    availabilityWriteRepository.save(availability);
                }
            }
        }
    }
}
