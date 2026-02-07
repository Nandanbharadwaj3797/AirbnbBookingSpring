-- V3: Create Airbnb table
CREATE TABLE IF NOT EXISTS airbnb (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    location VARCHAR(255),
    price_per_night BIGINT NOT NULL
);
