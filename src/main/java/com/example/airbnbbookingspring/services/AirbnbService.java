package com.example.airbnbbookingspring.services;

import com.example.airbnbbookingspring.models.Airbnb;
import com.example.airbnbbookingspring.repositories.writes.AirbnbWriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirbnbService implements IAirbnbService {
    @Autowired
    private AirbnbWriteRepository airbnbWriteRepository;

    @Override
    public List<Airbnb> getAllAirbnbs() {
        return airbnbWriteRepository.findAll();
    }

    @Override
    public Optional<Airbnb> getAirbnbById(Long id) {
        return airbnbWriteRepository.findById(id);
    }

    @Override
    public Airbnb createAirbnb(Airbnb airbnb) {
        return airbnbWriteRepository.save(airbnb);
    }

    @Override
    public Optional<Airbnb> updateAirbnb(Long id, Airbnb airbnbDetails) {
        Optional<Airbnb> optionalAirbnb = airbnbWriteRepository.findById(id);
        if (optionalAirbnb.isEmpty()) {
            return Optional.empty();
        }
        Airbnb airbnb = optionalAirbnb.get();
        airbnb.setName(airbnbDetails.getName());
        airbnb.setDescription(airbnbDetails.getDescription());
        airbnb.setLocation(airbnbDetails.getLocation());
        airbnb.setPricePerNight(airbnbDetails.getPricePerNight());
        Airbnb updated = airbnbWriteRepository.save(airbnb);
        return Optional.of(updated);
    }

    @Override
    public boolean deleteAirbnb(Long id) {
        if (!airbnbWriteRepository.existsById(id)) {
            return false;
        }
        airbnbWriteRepository.deleteById(id);
        return true;
    }
}
