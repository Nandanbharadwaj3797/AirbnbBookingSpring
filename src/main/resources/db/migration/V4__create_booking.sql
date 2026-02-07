-- V4: Create Booking table
CREATE TABLE IF NOT EXISTS booking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    airbnb_id BIGINT NOT NULL,
    total_price DOUBLE NOT NULL,
    booking_status VARCHAR(50) NOT NULL,
    idempotency_key VARCHAR(255) UNIQUE,
    check_in_date DATE,
    check_out_date DATE,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_booking_airbnb FOREIGN KEY (airbnb_id) REFERENCES airbnb(id)
);
