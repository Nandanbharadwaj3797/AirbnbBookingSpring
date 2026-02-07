package com.example.airbnbbookingspring.services;

import com.example.airbnbbookingspring.models.Availability;
import com.example.airbnbbookingspring.repositories.writes.AvailabilityWriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvailabilityService implements IAvailabilityService {
    @Autowired
    private AvailabilityWriteRepository availabilityWriteRepository;

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
}
