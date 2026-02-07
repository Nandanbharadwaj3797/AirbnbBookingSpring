package com.example.airbnbbookingspring.services;

import java.util.Optional;

import com.example.airbnbbookingspring.models.BookingStatus;
import org.springframework.stereotype.Service;

import com.example.airbnbbookingspring.models.Booking;
import com.example.airbnbbookingspring.models.Airbnb;
import com.example.airbnbbookingspring.models.User;
import com.example.airbnbbookingspring.models.readModels.BookingReadModel;
import com.example.airbnbbookingspring.repositories.reads.RedisReadRepository;
import com.example.airbnbbookingspring.repositories.writes.BookingWriteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdempotencyService implements IIdempotencyService {

    private final RedisReadRepository redisReadRepository;
    private final BookingWriteRepository bookingWriteRepository;

    @Override
    public boolean isIdempotencyKeyUsed(String idempotencyKey) {
        return this.findBookingByIdempotencyKey(idempotencyKey).isPresent();
    }

    @Override
    public Optional<Booking> findBookingByIdempotencyKey(String idempotencyKey) {

        BookingReadModel bookingReadModel = redisReadRepository.findBookingByIdempotencyKey(idempotencyKey);

        if (bookingReadModel != null) {
            // TODO: move it to a mapper/adapter
            Booking booking = Booking.builder()
                    .id(bookingReadModel.getId())
                    .airbnb(bookingReadModel.getAirbnbId() != null
                            ? Airbnb.builder().id(bookingReadModel.getAirbnbId()).build()
                            : null)
                    .user(bookingReadModel.getUserId() != null ? User.builder().id(bookingReadModel.getUserId()).build()
                            : null)
                    .totalPrice(bookingReadModel.getTotalPrice())
                    .bookingStatus(BookingStatus.valueOf(bookingReadModel.getBookingStatus()))
                    .idempotencyKey(bookingReadModel.getIdempotencyKey())
                    .checkInDate(bookingReadModel.getCheckInDate())
                    .checkOutDate(bookingReadModel.getCheckOutDate())
                    .build();

            return Optional.of(booking);
        }

        return bookingWriteRepository.findByIdempotencyKey(idempotencyKey);
    }
}