package com.example.airbnbbookingspring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Airbnb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String location;

    @Column(nullable = false)
    private Long pricePerNight;

    @OneToMany(mappedBy = "airbnb", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Booking> bookings;
}