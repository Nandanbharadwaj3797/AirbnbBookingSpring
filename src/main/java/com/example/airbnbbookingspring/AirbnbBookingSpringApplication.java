package com.example.airbnbbookingspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirbnbBookingSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirbnbBookingSpringApplication.class, args);
    }

}
