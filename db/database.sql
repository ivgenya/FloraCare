-- CREATE DATABASE flora_care;

CREATE TABLE user (
    id BINARY(16) NOT NULL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    google_id VARCHAR(255),
    avatar_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE room (
    id BINARY(16) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id BINARY(16) NOT NULL,
    image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE plant (
    id BINARY(16) NOT NULL PRIMARY KEY,
    room_id BINARY(16),
    name VARCHAR(255) NOT NULL,
    min_temperature INT,
    max_temperature INT,
    min_humidity INT,
    max_humidity INT,
    watering_frequency INT,
    fertilizing_frequency INT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE CASCADE
);

CREATE TABLE plant_photo (
    id BINARY(16) NOT NULL PRIMARY KEY,
    plant_id BINARY(16),
    image VARCHAR(255),
    is_main BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (plant_id) REFERENCES plant(id) ON DELETE CASCADE
);

CREATE TABLE device (
    id BINARY(16) NOT NULL PRIMARY KEY,
    room_id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    mac_address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE CASCADE
);

CREATE TABLE schedule (
    id BINARY(16) NOT NULL PRIMARY KEY,
    plant_id BINARY(16),
    watering_date DATE,
    is_watered BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (plant_id) REFERENCES plant(id) ON DELETE CASCADE
);
