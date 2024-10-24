-- CREATE DATABASE flora_care;

CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    google_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE room (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id INT NOT NULL,
    image BLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE plant (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT,
    name VARCHAR(255) NOT NULL,
    min_temperature INT,
    max_temperature INT,
    min_humidity INT,
    max_humidity INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE CASCADE
);

CREATE TABLE plant_photo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plant_id INT,
    image BLOB,
    FOREIGN KEY (plant_id) REFERENCES plant(id) ON DELETE CASCADE
);

CREATE TABLE device (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    mac_address VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE CASCADE
);

CREATE TABLE schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plant_id INT,
    watering_date DATE,
    is_watered BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (plant_id) REFERENCES plant(id) ON DELETE CASCADE
);

