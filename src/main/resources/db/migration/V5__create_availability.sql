-- V5: Create Availability table
CREATE TABLE IF NOT EXISTS availability (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    airbnb_id BIGINT NOT NULL,
    date DATE NOT NULL,
    booking_id BIGINT,
    CONSTRAINT fk_availability_airbnb FOREIGN KEY (airbnb_id) REFERENCES airbnb(id),
    CONSTRAINT fk_availability_booking FOREIGN KEY (booking_id) REFERENCES booking(id)
);
