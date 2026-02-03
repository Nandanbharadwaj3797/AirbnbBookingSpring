package com.example.airbnbbookingspring.repositories.writes;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.airbnbbookingspring.models.Airbnb;

import java.util.Optional;

@Repository
public interface AirbnbWriteRepository extends JpaRepository<Airbnb, Long> {

    Optional<Airbnb> findById(Long id);

}