package com.example.airbnbbookingspring.repositories.writes;

import java.time.LocalDate;
import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.airbnbbookingspring.models.Availability;
import com.example.airbnbbookingspring.models.Booking;

@Repository
public interface AvailabilityWriteRepository extends JpaRepository<Availability, Long> {

    // ---- READ QUERIES ----

    List<Availability> findByBooking_Id(Long bookingId);

    List<Availability> findByAirbnb_Id(Long airbnbId);

    List<Availability> findByAirbnb_IdAndDateBetween(
            Long airbnbId,
            LocalDate startDate,
            LocalDate endDate);

    boolean existsByAirbnb_IdAndDate(Long airbnbId, LocalDate date);

    Long countByAirbnb_IdAndDateBetweenAndBookingIsNotNull(
            Long airbnbId,
            LocalDate startDate,
            LocalDate endDate);

    // ---- WRITE QUERY ----

    @Modifying
    @Transactional
    @Query("""
                    UPDATE Availability a
                    SET a.booking = :booking
                    WHERE a.airbnb.id = :airbnbId
                    AND a.date BETWEEN :startDate AND :endDate
            """)
    int updateBookingByAirbnbAndDateRange(
            Booking booking,
            Long airbnbId,
            LocalDate startDate,
            LocalDate endDate);
}
