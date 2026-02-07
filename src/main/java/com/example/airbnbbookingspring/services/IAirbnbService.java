package com.example.airbnbbookingspring.services;

import com.example.airbnbbookingspring.models.Airbnb;
import java.util.List;
import java.util.Optional;

public interface IAirbnbService {
    List<Airbnb> getAllAirbnbs();

    Optional<Airbnb> getAirbnbById(Long id);

    Airbnb createAirbnb(Airbnb airbnb);

    Optional<Airbnb> updateAirbnb(Long id, Airbnb airbnbDetails);

    boolean deleteAirbnb(Long id);
}
