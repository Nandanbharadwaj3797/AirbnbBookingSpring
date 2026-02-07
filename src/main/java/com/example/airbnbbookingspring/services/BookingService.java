
package com.example.airbnbbookingspring.services;

import com.example.airbnbbookingspring.services.status.BookingStatusUpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.airbnbbookingspring.models.BookingStatus;
import com.example.airbnbbookingspring.saga.SagaEventSender;
import org.springframework.stereotype.Service;

import com.example.airbnbbookingspring.dtos.CreateBookingRequest;
import com.example.airbnbbookingspring.dtos.UpdateBookingRequest;
import com.example.airbnbbookingspring.models.Airbnb;
import com.example.airbnbbookingspring.models.Availability;
import com.example.airbnbbookingspring.models.User;
import com.example.airbnbbookingspring.models.Booking;
import com.example.airbnbbookingspring.repositories.reads.RedisWriteRepository;
import com.example.airbnbbookingspring.repositories.writes.AirbnbWriteRepository;
import com.example.airbnbbookingspring.repositories.writes.AvailabilityWriteRepository;
import com.example.airbnbbookingspring.repositories.writes.BookingWriteRepository;
import com.example.airbnbbookingspring.services.concurrency.ConcurrencyControlStrategy;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for creating and updating Airbnb bookings, with validation and event
 * publishing.
 */

@Service
@Slf4j

public class BookingService implements IBookingService {

    private final BookingWriteRepository bookingWriteRepository;
    private final AvailabilityWriteRepository availabilityWriteRepository;
    private final AirbnbWriteRepository airbnbWriteRepository;
    private final ConcurrencyControlStrategy concurrencyControlStrategy;
    private final RedisWriteRepository redisWriteRepository;
    private final IdempotencyService idempotencyService;
    private final SagaEventSender sagaEventSender;

    private final List<BookingStatusUpdateHandler> statusUpdateHandlers;

    @Autowired
    public BookingService(
            BookingWriteRepository bookingWriteRepository,
            AvailabilityWriteRepository availabilityWriteRepository,
            AirbnbWriteRepository airbnbWriteRepository,
            ConcurrencyControlStrategy concurrencyControlStrategy,
            RedisWriteRepository redisWriteRepository,
            IdempotencyService idempotencyService,
            SagaEventSender sagaEventSender,
            List<BookingStatusUpdateHandler> statusUpdateHandlers) {
        this.bookingWriteRepository = bookingWriteRepository;
        this.availabilityWriteRepository = availabilityWriteRepository;
        this.airbnbWriteRepository = airbnbWriteRepository;
        this.concurrencyControlStrategy = concurrencyControlStrategy;
        this.redisWriteRepository = redisWriteRepository;
        this.idempotencyService = idempotencyService;
        this.sagaEventSender = sagaEventSender;
        this.statusUpdateHandlers = statusUpdateHandlers;
    }

    /**
     * Creates a new booking after validating input and availability.
     * 
     * @param createBookingRequest Booking creation request
     * @return Created Booking
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    @Transactional
    public Booking createBooking(CreateBookingRequest createBookingRequest) {
        Airbnb airbnb = airbnbWriteRepository.findById(createBookingRequest.getAirbnbId())
                .orElseThrow(() -> new IllegalArgumentException("Airbnb not found"));
        User user = new User();
        user.setId(createBookingRequest.getUserId());
        if (createBookingRequest.getCheckInDate() == null || createBookingRequest.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }
        if (createBookingRequest.getCheckInDate().isAfter(createBookingRequest.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }
        if (createBookingRequest.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date must be today or in the future");
        }
        concurrencyControlStrategy.lockAndCheckAvailability(
                airbnb.getId(),
                createBookingRequest.getCheckInDate(),
                createBookingRequest.getCheckOutDate(),
                createBookingRequest.getUserId());
        long nights = ChronoUnit.DAYS.between(createBookingRequest.getCheckInDate(),
                createBookingRequest.getCheckOutDate());
        double pricePerNight = airbnb.getPricePerNight();
        double totalPrice = pricePerNight * nights;
        String idempotencyKey = UUID.randomUUID().toString();
        log.info(
                "Creating booking for Airbnb {} with check-in date {} and check-out date {} and total price {} and idempotency key {}",
                airbnb.getId(), createBookingRequest.getCheckInDate(), createBookingRequest.getCheckOutDate(),
                totalPrice, idempotencyKey);
        Booking booking = Booking.builder()
                .airbnb(airbnb)
                .user(user)
                .totalPrice(totalPrice)
                .idempotencyKey(idempotencyKey)
                .bookingStatus(BookingStatus.PENDING)
                .checkInDate(createBookingRequest.getCheckInDate())
                .checkOutDate(createBookingRequest.getCheckOutDate())
                .build();
        booking = bookingWriteRepository.save(booking);
        redisWriteRepository.writeBookingReadModel(booking);
        return booking;
    }

    /**
     * Updates a booking's status and triggers appropriate saga events.
     * 
     * @param updateBookingRequest Booking update request
     * @return Updated Booking
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    @Transactional
    public Booking updateBooking(UpdateBookingRequest updateBookingRequest) {
        log.info("Updating booking for idempotency key {}", updateBookingRequest.getIdempotencyKey());
        Booking booking = idempotencyService.findBookingByIdempotencyKey(updateBookingRequest.getIdempotencyKey())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        log.info("Booking found for idempotency key {}", updateBookingRequest.getIdempotencyKey());
        log.info("Booking status: {}", booking.getBookingStatus());
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Booking is not pending");
        }
        if (updateBookingRequest.getBookingStatus() == null) {
            throw new IllegalArgumentException("Booking status is required");
        }
        boolean handled = false;
        for (var handler : statusUpdateHandlers) {
            if (handler.supports(updateBookingRequest, booking)) {
                handler.handle(updateBookingRequest, booking);
                handled = true;
                break;
            }
        }
        if (!handled) {
            throw new IllegalArgumentException("Unsupported booking status update");
        }
        return booking;
    }
}