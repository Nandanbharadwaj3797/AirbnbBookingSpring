package com.example.airbnbbookingspring.models.readModels;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingReadModel {

    private Long id;

    private Long airbnbId;

    private Long userId;

    private double totalPrice;

    private String bookingStatus;

    private String idempotencyKey;

    private LocalDate  checkInDate;

    private LocalDate checkOutDate;

}