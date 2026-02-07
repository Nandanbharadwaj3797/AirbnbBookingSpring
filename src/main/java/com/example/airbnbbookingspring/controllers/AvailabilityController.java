package com.example.airbnbbookingspring.controllers;

import com.example.airbnbbookingspring.models.Availability;
import com.example.airbnbbookingspring.services.IAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    @Autowired
    private IAvailabilityService availabilityService;

    @GetMapping
    public List<Availability> getAll() {
        return availabilityService.getAllAvailabilities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Availability> getById(@PathVariable Long id) {
        return availabilityService.getAvailabilityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Availability create(@RequestBody Availability availability) {
        return availabilityService.createAvailability(availability);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Availability> update(@PathVariable Long id, @RequestBody Availability availability) {
        return availabilityService.updateAvailability(id, availability)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!availabilityService.deleteAvailability(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
