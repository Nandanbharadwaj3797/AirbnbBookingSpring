package com.example.airbnbbookingspring.repositories.writes;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.airbnbbookingspring.models.Availability;

@Repository
public interface AvailabilityWriteRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByBookingId(Long bookingId);

    List<Availability> findByAirbnbId(String airbnbId);


}