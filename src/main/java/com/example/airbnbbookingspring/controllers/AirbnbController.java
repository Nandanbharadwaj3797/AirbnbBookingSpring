package com.example.airbnbbookingspring.controllers;

import com.example.airbnbbookingspring.models.Airbnb;
import com.example.airbnbbookingspring.services.IAirbnbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/airbnbs")
public class AirbnbController {
    @Autowired
    private IAirbnbService airbnbService;

    @GetMapping
    public List<Airbnb> getAllAirbnbs() {
        return airbnbService.getAllAirbnbs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Airbnb> getAirbnbById(@PathVariable Long id) {
        Optional<Airbnb> airbnb = airbnbService.getAirbnbById(id);
        return airbnb.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Airbnb createAirbnb(@RequestBody Airbnb airbnb) {
        return airbnbService.createAirbnb(airbnb);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Airbnb> updateAirbnb(@PathVariable Long id, @RequestBody Airbnb airbnbDetails) {
        Optional<Airbnb> updated = airbnbService.updateAirbnb(id, airbnbDetails);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirbnb(@PathVariable Long id) {
        if (!airbnbService.deleteAirbnb(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
